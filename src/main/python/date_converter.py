import jep
from datetime import datetime
from java.util import Date

def date_to_datetime(jdate):
    return datetime.utcfromtimestamp(jdate.getTime()/1000)

jep.setJavaToPythonConverter(Date, date_to_datetime)