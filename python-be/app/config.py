from functools import lru_cache
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    DB_USERNAME: str
    DB_HOST: str
    DB_PASSWORD: str
    DB_NAME: str
    DB_PORT: int
    JSON_DATA: str

    class Config:
        extra = "ignore"
        env_file = ".env"
        env_file_encoding = "utf-8"


@lru_cache
def get_settings():
    return Settings()


settings = get_settings()
