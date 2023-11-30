import psycopg2 
class Connector:
    server = '45.9.73.84' 
    database = 'Taxi' 
    username = 'postgres'
    password = '1A2s#d4f'
    def __init__(self) -> None:
        self.conn = psycopg2.connect(dbname = 'Taxi', user=self.username, password=self.password, host = self.server, port=5432)
