from fastapi import FastAPI

app = FastAPI()


# 서버 최초 구동 시에 데이터베이스와 서버를 연결하는 작업
@app.on_event("startup")
def on_startup():
    from app import model
    from app.database import engine

    model.Base.metadata.create_all(bind=engine)
