from pydantic import BaseModel, Field, TypeAdapter
from typing import List
from google import genai
import os
from dotenv import load_dotenv

load_dotenv()
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")


class AnswerRecord(BaseModel):
    answer: str = Field(..., description="The text of the answer option")
    is_correct: bool = Field(..., description="Indicates whether this answer is correct")

class SingleAnswerQuestion(BaseModel):
    single_answer_question: str = Field(..., description="The text of the single-answer question")
    points: float = Field(..., description="Points awarded for answering the question correctly")
    answers: List[AnswerRecord] = Field(..., description="List of answer options for the single-answer question")

class MultiAnswerQuestion(BaseModel):
    multi_answer_question: str = Field(..., description="The text of the multi-answer question")
    points: float = Field(..., description="Points awarded for answering the question correctly")
    answers: List[AnswerRecord] = Field(..., description="List of answer options for the multi-answer question")

class QuizRecord(BaseModel):
    single_answer_questions: List[SingleAnswerQuestion] = Field(..., description="List of single-answer questions in the quiz")
    multi_answer_questions: List[MultiAnswerQuestion] = Field(..., description="List of multi-answer questions in the quiz")

def generate_quiz(context, single, multi, system_text, user_text=None):
    user_message = user_text.format(context=context, single=single, multi=multi)
    client = genai.Client(api_key=GEMINI_API_KEY)

    response = client.models.generate_content(
        model="gemini-2.0-flash",
        config={
            'response_mime_type': 'application/json',
            'response_schema': QuizRecord,
            'system_instruction': system_text
        },
        contents=user_message,
    )

    return QuizRecord.parse_raw(response.text).dict()
