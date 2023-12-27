from sqlalchemy import Column, Integer, DateTime, func, String
from app.database import Base


class BaseMin:
    id = Column(Integer, primary_key=True, index=True)
    created_at = Column(DateTime, nullable=False, default=func.now())
    updated_at = Column(DateTime, nullable=False, default=func.now(), onupdate=func.now())


class Cafe(Base, BaseMin):
    __tablename__ = "cafe"

    name = Column(String(30), nullable=False, unique=True)
    type = Column(String(10), nullable=False)
    address_old = Column(String(60), nullable=False)
    address_new = Column(String(60), nullable=False)
    tel = Column(String(12), nullable=True)
    business_hours = Column(String(15), nullable=True)
    business_day = Column(String(15), nullable=True)
