from flask import Flask, jsonify, request, abort
from flask_restful import Api
import ConnectToDB


app = Flask(__name__)
api = Api(app=app)
conn = ConnectToDB.Connector().conn


@app.route('/Rybn/ActiveOrders', methods=['GET'])
def get_active_orders():
    cursor = conn.cursor()
    cursor.execute("""select orderid, orderdate, orderfirstaddress,
        orderdestinationaddress, s.statusname from Orders o, Statuses s 
        where o.statusid=s.statusid and s.statusname='Активен'""")
    columns = [column[0] for column in cursor.description]
    results = []
    rows = cursor.fetchall()
    for row in rows:
        results.append(dict(zip(columns, row)))
    return jsonify(results)

@app.route('/Rybn/TakeAnOrder/<int:order_id>/<int:driver_id>', methods=['POST'])
def take_an_order(order_id, driver_id):
    cursor = conn.cursor()
    cursor.execute("""update orders o set
      o.statusid=2,
      o.driverid=%(driver_id)s
      where o.orderid=%(order_id)s""", {'order_id':order_id, 'driver_id':driver_id})

@app.route("/Rybn/DeclineOrder/<int:order_id>/<string:reason>", methods=['PUT'])
def decline_order(order_id, reason):
    cursor = conn.cursor()
    cursor.execute("""
        
insert into canceled_orders(orderid, orderdate,
						   orderfirstaddress,
						   orderdestinationaddress,
						   driver, cancelation_reason) select 
						   o.orderid, o.orderdate, o.orderfirstaddress,
						   o.orderdestinationaddress, concat(
							   d.driverlastname,' '
						   ,d.driverfirstname,' ',
						   d.drivermiddlename)
						   , %(reason)s from
						   orders o inner join
						   drivers d on o.driverid=d.driverid 
						   where o.orderid = %(orderid)s;
delete from orders o where o.orderid = %(orderid)s					   

    """, {'orderid':order_id, 'reason':reason})


@app.route("/Rybn/Login/<string:phone>/<string:password>", methods=['GET'])
def login(phone, password):
    cursor = conn.cursor()
    cursor.execute("""select driverid, driverfirstname, driverlastname, drivermiddlename,
      driverphone, encode(driverphoto, 'hex') as driverphoto from drivers d
where d.driverphone=%(phone)s and d.driverpassword=%(password)s""", {'phone':phone, 'password':password})
    row = cursor.fetchone()
    columns = [column[0] for column in cursor.description]

    result = {}
    if row!=None:
        for i in range(len(columns)):
            result[columns[i]] = row[i]
    else:
        return abort(403)
    return result

@app.route("/Rybn/EndOrder/<int:order_id>/<float:cost>", methods=['PUT'])
def end_order(order_id, cost):
    cursor = conn.cursor()
    cursor.execute("""update orders o set o.statusid=3, o.ordercost=%(cost)s where o.orderid=%(order_id)s""", {'order_id':order_id, 'cost':cost})

@app.route("/Rybn/Arrived/<int:order_id>", methods=['PUT'])
def arrived(order_id):
    cursor = conn.cursor()
    cursor.execute("""update orders o set o.statusid=4 where o.orderid=%(order_id)s""", {'order_id':order_id})

@app.route("/Rybn/GetTodaysStats/<int:driver_id>", methods=['GET'])
def get_todays_stats(id):
    cursor = conn.cursor()
    cursor.execute("""select count(*) as EndedOrders, sum(o.order_cost) as SumCost from orders o, statuses s
where o.statusid=s.statusid and
s.statusname='Завершён' and
date_trunc('day', orderdate)=date_trunc('day', current_timestamp) and o.driverid =%s""", id)
    columns = [column[0] for column in cursor.description]
    results = []
    rows = cursor.fetchall()
    for row in rows:
        results.append(dict(zip(columns, row)))
    return jsonify({'data': results})

@app.route("/Rybn/GetMonthlyStats/<int:driver_id>", methods=['GET'])
def get_monthly_stats(id):
    cursor = conn.cursor()
    cursor.execute("""select count(*) as EndedOrders, sum(o.order_cost) as SumCost from orders o, statuses s
where o.statusid=s.statusid and
s.statusname='Завершён' and
date_trunc('month', orderdate)=date_trunc('month', current_timestamp) and o.driverid = %s""", id)
    columns = [column[0] for column in cursor.description]
    results = []
    rows = cursor.fetchall()
    for row in rows:
        results.append(dict(zip(columns, row)))
    return jsonify({'data': results})

@app.route("/Rybn/driverHasActiveOrder/<int:driver_id>", methods=['GET'])
def has_active_order(driver_id):
    cursor = conn.cursor()
    cursor.execute("""select orderid, orderdate, orderfirstaddress,
        orderdestinationaddress, s.statusname from Orders o, Statuses s 
        where o.statusid=s.statusid and s.statusname='Активен' and o.driverid=%(driverid)s""", {'driverid':driver_id})
    row = cursor.fetchone()
    columns = [column[0] for column in cursor.description]

    result = {}
    if row != None:
        for i in range(len(columns)):
            result[columns[i]] = row[i]
    else:
        return abort(404)
    return result

if __name__ == "__main__":
    app.run(port=4129, debug=True, host='localhost')
