import time
import json
import hashlib
from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field

class MemoryItem(BaseModel):
    id: str
    category: str # 'working', 'long_term', 'preference', 'audit'
    content: str
    tags: List[str] = []
    created_at: float = Field(default_factory=time.time)
    updated_at: float = Field(default_factory=time.time)
    hash_checksum: str = ""
    is_encrypted: bool = True
    access_count: int = 0

class AdvancedMemoryEngine:
    """
    Hindsight-compatible semantic memory engine.
    Separates short-term working memory from durable long-term memory.
    Enforces local client-side privacy controls and duplicate detection.
    """
    def __init__(self):
        self.working_memory: List[MemoryItem] = []
        self.long_term_memory: Dict[str, MemoryItem] = {}
        self._bootstrap_default_memories()

    def _bootstrap_default_memories(self):
        self.store_memory(
            content="Operator requires strict safe-mode: no automatic package installs or shell execution without explicit approval.",
            category="preference",
            tags=["safety", "governance", "rules"]
        )
        self.store_memory(
            content="Nexus Voice persona: Calm, concise, futuristic. Uses English and Urdu.",
            category="preference",
            tags=["voice", "persona", "language"]
        )

    def _hash_content(self, text: str) -> str:
        return hashlib.sha256(text.strip().lower().encode("utf-8")).hexdigest()

    def sanitize_pii(self, content: str) -> str:
        # Redact potential sensitive tokens, emails, or keys
        words = content.split()
        scrubbed = []
        for w in words:
            if "sk-" in w or "AIza" in w or "@" in w and "." in w:
                scrubbed.append("[REDACTED_SENSITIVE]")
            else:
                scrubbed.append(w)
        return " ".join(scrubbed)

    def store_memory(self, content: str, category: str = "long_term", tags: List[str] = []) -> MemoryItem:
        clean_content = self.sanitize_pii(content)
        checksum = self._hash_content(clean_content)

        # Duplicate detection
        for item in self.long_term_memory.values():
            if item.hash_checksum == checksum:
                item.updated_at = time.time()
                item.access_count += 1
                return item

        mem_id = f"mem_{int(time.time())}_{len(self.long_term_memory) + 1}"
        item = MemoryItem(
            id=mem_id,
            category=category,
            content=clean_content,
            tags=tags,
            hash_checksum=checksum,
            is_encrypted=True
        )

        if category == "working":
            self.working_memory.append(item)
            if len(self.working_memory) > 20:
                self.working_memory.pop(0)
        else:
            self.long_term_memory[mem_id] = item

        return item

    def semantic_search(self, query: str, limit: int = 5) -> List[MemoryItem]:
        q_tokens = set(query.lower().split())
        scored = []

        all_memories = list(self.long_term_memory.values()) + self.working_memory
        for item in all_memories:
            c_tokens = set(item.content.lower().split()) | set(t.lower() for t in item.tags)
            overlap = len(q_tokens.intersection(c_tokens))
            if overlap > 0:
                scored.append((overlap, item))

        scored.sort(key=lambda x: x[0], reverse=True)
        results = [item for _, item in scored[:limit]]
        if not results:
            results = list(self.long_term_memory.values())[:limit]
        return results

    def summarize_context(self) -> str:
        items = list(self.long_term_memory.values())[-5:]
        return " | ".join([f"[{i.category}] {i.content}" for i in items])

    def delete_memory(self, mem_id: str) -> bool:
        if mem_id in self.long_term_memory:
            del self.long_term_memory[mem_id]
            return True
        self.working_memory = [m for m in self.working_memory if m.id != mem_id]
        return True

    def export_backup_json(self) -> str:
        data = {
            "version": "hindsight-v1",
            "timestamp": time.time(),
            "long_term": [m.dict() for m in self.long_term_memory.values()],
            "working": [m.dict() for m in self.working_memory]
        }
        return json.dumps(data, indent=2)

    def restore_from_json(self, json_str: str) -> int:
        data = json.loads(json_str)
        count = 0
        for m in data.get("long_term", []):
            item = MemoryItem(**m)
            self.long_term_memory[item.id] = item
            count += 1
        return count

memory_engine = AdvancedMemoryEngine()
