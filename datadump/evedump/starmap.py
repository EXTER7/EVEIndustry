class SolarSystemRegion:
  def __init__(self,row):
    self.id = int(row[0])
    self.name = str(row[1])

  def get_list(dbc):
    query = "\
      SELECT \
        regionID, \
        regionName \
      FROM \
        mapRegions \
      ORDER BY \
        regionName \
    "
    dbc.execute(query)
    table = dbc.fetchall()
    res = []
    for row in table:
      res.append(SolarSystemRegion(row))
    return res


class SolarSystem:
  def __init__(self,row):
    self.id = int(row[0])
    self.name = str(row[1])
    self.region = int(row[2])
    self.sec_status = float(row[3])

  def get_list(dbc):
    query = "\
      SELECT \
        solarSystemID, \
        solarSystemName, \
        regionID, \
        security \
      FROM \
        mapSolarSystems \
      ORDER BY \
        solarSystemName \
      "
    dbc.execute(query)
    table = dbc.fetchall()
    res = []
    for row in table:
      res.append(SolarSystem(row))
    return res

