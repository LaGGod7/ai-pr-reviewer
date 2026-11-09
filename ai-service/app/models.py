from pydantic import BaseModel,Field

class FileDiff(BaseModel):
    filename: str
    status:str
    patch:str | None = None

class ReviewRequest(BaseModel):
    title:str
    files:list[FileDiff] = Field(min_length =1)

class ReviewResponse(BaseModel):
    review:str
    files_reviewed:int
    truncated:bool