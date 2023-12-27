from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from app.config import settings

# MySQL과 연결할 Engine 생성
engine = create_engine(
    "mysql+pymysql://{username}:{password}@{host}:{port}/{name}".format(
        username=settings.DB_USERNAME,
        password=settings.DB_PASSWORD,
        host=settings.DB_HOST,
        port=settings.DB_PORT,
        name=settings.DB_NAME,
    )
)

# SQLAlchemy가 데이터베이스와 상호작용할 때 사용하는 Session생성
SessionLocal = sessionmaker(
    bind=engine,
    autocommit=False,
    autoflush=False,
)

# 이 후 데이터베이스의 테이블을 정의할 때 이 Base를 상속받는다.
Base = declarative_base()


# 엔드포인트에서 Session 객체를 생성할 때 종속성 주입으로 이 함수의 리턴 값을 전달한다.
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
