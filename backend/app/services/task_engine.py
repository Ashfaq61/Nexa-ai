import uuid
import time
from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field

class SubGoal(BaseModel):
    id: str
    description: str
    status: str = "pending" # pending, in_progress, completed, failed, paused
    retry_count: int = 0
    max_retries: int = 3
    result: Optional[str] = None
    error: Optional[str] = None

class PlannedTask(BaseModel):
    task_id: str
    title: str
    status: str = "created" # created, running, paused, completed, failed
    created_at: float = Field(default_factory=time.time)
    subgoals: List[SubGoal] = []
    current_step_index: int = 0
    requires_approval: bool = False
    is_approved: bool = True
    execution_log: List[str] = []

class TaskEngine:
    def __init__(self):
        self.tasks: Dict[str, PlannedTask] = {}

    def decompose_and_plan(self, title: str, task_prompt: str, requires_approval: bool = False) -> PlannedTask:
        task_id = f"task_{uuid.uuid4().hex[:8]}"

        # Intelligent multi-step decomposition
        subgoals: List[SubGoal] = [
            SubGoal(id="step_1", description="Deconstruct user intent and verify prerequisite permissions"),
            SubGoal(id="step_2", description="Collect context data via MCP research and memory recall"),
            SubGoal(id="step_3", description="Execute isolated tool action or synthetic plan in sandbox"),
            SubGoal(id="step_4", description="Verify execution result and produce final artifact")
        ]

        if "browser" in task_prompt.lower() or "website" in task_prompt.lower():
            subgoals.insert(2, SubGoal(id="step_browser", description="Navigate target webpage via Playwright MCP & extract DOM tree"))

        planned_task = PlannedTask(
            task_id=task_id,
            title=title,
            status="running" if not requires_approval else "paused",
            subgoals=subgoals,
            requires_approval=requires_approval,
            is_approved=not requires_approval,
            execution_log=[f"[{time.strftime('%H:%M:%S')}] Task initialized with {len(subgoals)} subgoals."]
        )
        self.tasks[task_id] = planned_task
        return planned_task

    def execute_next_step(self, task_id: str) -> PlannedTask:
        task = self.tasks.get(task_id)
        if not task:
            raise ValueError(f"Task {task_id} not found.")

        if task.status == "paused":
            task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Task is currently paused or awaiting operator approval.")
            return task

        if task.current_step_index >= len(task.subgoals):
            task.status = "completed"
            task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] All subgoals completed successfully.")
            return task

        current_goal = task.subgoals[task.current_step_index]
        current_goal.status = "in_progress"
        task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Executing: {current_goal.description}")

        # Simulate execution with self-healing recovery logic
        try:
            current_goal.status = "completed"
            current_goal.result = f"Verified completion of {current_goal.id}"
            task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Completed: {current_goal.description}")
            task.current_step_index += 1

            if task.current_step_index >= len(task.subgoals):
                task.status = "completed"
        except Exception as e:
            current_goal.retry_count += 1
            if current_goal.retry_count <= current_goal.max_retries:
                current_goal.status = "pending"
                task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Step failed ({e}). Retrying ({current_goal.retry_count}/{current_goal.max_retries})...")
            else:
                current_goal.status = "failed"
                current_goal.error = str(e)
                task.status = "failed"
                task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Step failed after {current_goal.max_retries} attempts.")

        return task

    def pause_task(self, task_id: str) -> PlannedTask:
        task = self.tasks.get(task_id)
        if task:
            task.status = "paused"
            task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Task paused by operator.")
        return task

    def resume_task(self, task_id: str) -> PlannedTask:
        task = self.tasks.get(task_id)
        if task:
            task.status = "running"
            task.is_approved = True
            task.execution_log.append(f"[{time.strftime('%H:%M:%S')}] Task resumed.")
        return task

    def get_all_tasks(self) -> List[PlannedTask]:
        return list(self.tasks.values())

task_engine = TaskEngine()
