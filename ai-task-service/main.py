from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

# Request model
class TaskRequest(BaseModel):
    title: str
    description: str

@app.get("/")
def home():
    return {"message": "AI Service Running"}

#Core AI logic (rule based for now)
def analyze_task_logic(title, description):
    text = (title + " " + description).lower()

    #Priority logic
    if "urgent" in text or "asap" in text:
        priority = "HIGH"
    elif "later" in text:
        priority = "LOW"
    else:
        priority = "MEDIUM"

    # Effort estimation
    if len(description) > 100:
        effort = "HIGH"
    elif len(description) > 50:
        effort = "MEDIUM"
    else:
        effort = "LOW"

    # Simple Summary
    summary = description[:60]

    return {
        "suggested_priority": priority,
        "estimated_effort": effort,
        "summary": summary
    }

@app.post("/analyze-task")
def analyze_task(task: TaskRequest):
    return analyze_task_logic(task.title, task.description)