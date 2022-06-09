import pymysql

class MySql:
    """
    Wrapper the basic operation for database
    """

    db = None

    def connect(self):
        """
        For simplicity, we hardcode the connect.
        """
        try:
            self.db = pymysql.connect(
                host = "localhost",
                user = "root",
                password = "root",
                database = "dockerhub_info"
                )
        except:
            raise Exception("Error: Connection Failed")

    def insert(self, sql):
        try:
            cursor = self.db.cursor()
            cursor.execute(sql)
            self.db.commit()
            return True
        except:
            self.db.rollback()
            print("Error: Insertion Failed")
            return False

    def disconnect(self):
        self.db.close()
