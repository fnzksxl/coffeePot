import json
import re

from datetime import datetime
from sqlalchemy import create_engine, Table, MetaData
from sqlalchemy.orm import sessionmaker

from config import settings

json_data = settings.JSON_DATA

engine = create_engine(
    f"mysql+pymysql://{settings.DB_USERNAME}:{settings.DB_PASSWORD}@{settings.DB_HOST}/{settings.DB_NAME}"
)

metadata = MetaData()
your_table = Table("cafe", metadata, autoload_with=engine)

Session = sessionmaker(bind=engine)
session = Session()
with open(json_data, "r", encoding="utf-8") as f:
    json_data = json.load(f)
    for item in json_data:
        result = re.match(r"(.+) (\d{2}:\d{2}) ~ (\d{2}:\d{2})", item["영업시간"])
        if result:
            days, start_time_str, end_time_str = result.groups()
            business_hours = f"{start_time_str} ~ {end_time_str}"
            data = {
                "name": item["name"],
                "type": item["coffee_type"],
                "address_old": item["address1"],
                "address_new": item["address2"],
                "business_hours": business_hours if len(business_hours) > 5 else None,
                "business_day": days if len(days) else None,
                "tel": item["전화번호"] if not len(item["전화번호"]) else None,
                "created_at": datetime.now(),
                "updated_at": datetime.now(),
            }
            insert_stmt = your_table.insert().values(data)
            session.execute(insert_stmt)

            session.commit()
        else:
            data = {
                "name": item["name"],
                "type": item["coffee_type"],
                "address_old": item["address1"],
                "address_new": item["address2"],
                "business_hours": None,
                "business_day": None,
                "tel": item["전화번호"] if not len(item["전화번호"]) else None,
                "created_at": datetime.now(),
                "updated_at": datetime.now(),
            }
            insert_stmt = your_table.insert().values(data)
            session.execute(insert_stmt)

            session.commit()
