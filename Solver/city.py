__author__ = 'Henry'


class Particle(object):
	def __init__(self):
		pass


class Crossroad(object):
	traffic_lights = []

	def __init__(self, line):
		splitted = line.strip(',').split(',')
		traffic_lights = [int(x) for x in splitted]
