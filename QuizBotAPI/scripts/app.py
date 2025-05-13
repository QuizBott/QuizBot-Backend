from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
from quiz_generator import generate_quiz

app = FastAPI()

class QuizRequest(BaseModel):
    context: str
    single: int
    multi: int
    systemText: str
    userText: Optional[str] = None

@app.post("/generate")
def generate_quiz_endpoint(req: QuizRequest):
    try:
        result = generate_quiz(
            context=req.context,
            single=req.single,
            multi=req.multi,
            system_text=req.systemText,
            user_text=req.userText
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
