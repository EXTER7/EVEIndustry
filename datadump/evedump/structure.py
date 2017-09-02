import yaml
from .item import *

class Structure:
  def __init__(self,dbc,row):
    self.id = int(row[0])
    self.material = float(row[1])
    self.cost = float(row[2])
    self.time = float(row[3])
    self.service_slots = int(row[4])
    self.rig_slots = int(row[5])
    self.rig_size = int(row[6])


  def get_list(dbc):
    query = "\
      SELECT  \
          invTypes.typeID, \
          IFNULL((SELECT \
              dgmTypeAttributes.valueFloat \
          FROM \
              dgmTypeAttributes \
          WHERE \
              dgmTypeAttributes.attributeID = 2600 AND invTypes.typeID = dgmTypeAttributes.typeID), 1), \
          IFNULL((SELECT \
              dgmTypeAttributes.valueFloat \
          FROM \
              dgmTypeAttributes \
          WHERE \
              dgmTypeAttributes.attributeID = 2601 AND invTypes.typeID = dgmTypeAttributes.typeID), 1), \
          IFNULL((SELECT \
              dgmTypeAttributes.valueFloat \
          FROM \
              dgmTypeAttributes \
          WHERE \
              dgmTypeAttributes.attributeID = 2602 AND invTypes.typeID = dgmTypeAttributes.typeID), 1), \
          (SELECT \
              CAST(dgmTypeAttributes.valueFloat AS INT) \
          FROM \
              dgmTypeAttributes \
          WHERE \
              dgmTypeAttributes.attributeID = 2056 AND invTypes.typeID = dgmTypeAttributes.typeID), \
          (SELECT \
              CAST(dgmTypeAttributes.valueFloat AS INT) \
          FROM \
              dgmTypeAttributes \
          WHERE \
              dgmTypeAttributes.attributeID = 1137 AND invTypes.typeID = dgmTypeAttributes.typeID), \
          (SELECT \
              CAST(dgmTypeAttributes.valueFloat AS INT) \
          FROM \
              dgmTypeAttributes \
          WHERE \
              dgmTypeAttributes.attributeID = 1547 AND invTypes.typeID = dgmTypeAttributes.typeID) \
      FROM \
          invTypes \
      LEFT JOIN \
          invGroups ON invGroups.groupID = invTypes.groupID \
      WHERE \
          invGroups.categoryID = 65 AND invTypes.published = 1 \
      "
    dbc.execute(query)
  
    res = []
    table = dbc.fetchall()
    for row in table:
      print(row)
      res.append(Structure(dbc,row))
    return res

