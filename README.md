# QuizBot-Backend

This is the backend repository for the **QuizBot** application, responsible for managing quizzes, generating new ones using AI services, and providing endpoints for frontend integration.

## 🧭 API Request Mapping — `QuizController`

## ⚙️ Controller-to-Service Mapping

Each endpoint delegates business logic to the `QuizService`, maintaining a clean separation of concerns.

| Endpoint                  | Method            | Service Call                        |
|---------------------------|-------------------|-------------------------------------|
| `GET /api/quiz`           | `getQuizzes()`    | `quizService.getQuizzes()`          |
| `DELETE /api/quiz/{id}`   | `deleteQuiz()`    | `quizService.deleteQuiz()`          |
| `PUT /api/quiz/{id}`      | `updateQuiz()`    | `quizService.updateQuiz()`          |
| `POST /generate/ollama`   | `generateQuiz()`  | `quizService.generateQuizOllama()`  |
| `POST /generate/gemini`   | `generateQuizV2()`| `quizService.generateQuizGemini()`  |
