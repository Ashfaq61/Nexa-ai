export type AssistantState = 'disconnected' | 'connecting' | 'listening' | 'speaking';

export interface ToolCallPayload {
  name: string;
  args: Record<string, any>;
  callId: string;
}

export interface ToolDefinition {
  name: string;
  description: string;
  parameters: {
    type: string;
    properties: Record<string, { type: string; description: string }>;
    required?: string[];
  };
}
