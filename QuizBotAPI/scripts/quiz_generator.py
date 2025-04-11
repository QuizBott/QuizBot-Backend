import argparse
from pydantic import BaseModel, Field, TypeAdapter
from typing import List
from google import genai
import os
from dotenv import find_dotenv, load_dotenv

dotenv_path = find_dotenv()
load_dotenv(dotenv_path)
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")


class Answer(BaseModel):
    answer: str = Field(..., description="The text of the answer option")
    is_correct: bool = Field(..., description="Indicates whether this answer is correct")

class SingleAnswerQuestion(BaseModel):
    single_answer_question: str = Field(..., description="The text of the single-answer question")
    points: int = Field(..., description="Points awarded for answering the question correctly")
    answers: List[Answer] = Field(..., description="List of answer options for the single-answer question")

class MultiAnswerQuestion(BaseModel):
    multi_answer_question: str = Field(..., description="The text of the multi-answer question")
    points: int = Field(..., description="Points awarded for answering the question correctly")
    answers: List[Answer] = Field(..., description="List of answer options for the multi-answer question")

class QuizRecord(BaseModel):
    quiz_name: str = Field(..., description="The name or title of the quiz")
    single_answer_questions: List[SingleAnswerQuestion] = Field(..., description="List of single-answer questions in the quiz")
    multi_answer_questions: List[MultiAnswerQuestion] = Field(..., description="List of multi-answer questions in the quiz")

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--context', type=str, required=True)
    parser.add_argument('--single', type=int, required=True)
    parser.add_argument('--multi', type=int, required=True)
    parser.add_argument('--systemText',type=str,required=True)
    parser.add_argument('--userText',type=str,required=False)

    args = parser.parse_args()

    system_message = args.systemText

    user_message = args.userText.format(
        context=args.context,
        single=args.single,
        multi=args.multi
    )

    client = genai.Client(api_key=GEMINI_API_KEY)

    response = client.models.generate_content(
        model="gemini-2.0-flash",
        config={
            'response_mime_type': 'application/json',
            'response_schema': QuizRecord,
            'system_instruction': system_message
        },
        contents=user_message,
    )

    print(response.text)

if __name__ == '__main__':
    main()
