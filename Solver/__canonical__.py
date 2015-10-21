# coding=utf-8
import random
import itertools

__author__ = 'Henry'

"""
__canonical__.py contains the functions which does
not vary accordingly to the version of PSO used.
"""

import subprocess
import numpy as np


def swarm_fitness(x, n_cars, n_simulator_iterations, times_file, simulator_file, output_file, simulator_path):
	"""
	Calculates the fitness of every particle in the swarm.
	:param x: An array where each entry is the position of the i-th particle.
	:param n_cars: Number of cars in the simulation.
	:param n_simulator_iterations: Number of iterations in the simulation.
	:param times_file: The file where to store the traffic lights times.
	:param simulator_file: A pointer to the .jar simulator file.
	:param output_file: A file where the simulator will write the output.
	:param simulator_path: The path where the simulator is.
	:return: An array where each entry is the fitness of each particle in the swarm.
	"""

	n_individuals, lines, columns = x.shape

	fitness = np.empty(n_individuals, dtype=np.float)
	for i in xrange(n_individuals):
		fitness[i] = particle_fitness(x[i], n_cars, n_simulator_iterations, times_file, simulator_file, output_file, simulator_path)

	return fitness


def particle_fitness(x, n_cars, n_simulator_iterations, times_file, simulator_file, output_file, simulator_path):
	"""
	Calculates the fitness of a particle in the swarm.
	:param x: The particle position.
	:param n_cars: Number of cars in the simulation.
	:param n_simulator_iterations: Number of iterations in the simulation.
	:param times_file: The file where to store the traffic lights times.
	:param simulator_file: A pointer to the .jar simulator file.
	:param output_file: A file where the simulator will write the output.
	:param simulator_path: The path where the simulator is.
	:return: A float with the fitness of this particle.
	"""

	write_times(times_file, x)  # writes times to times file
	subprocess.call(['java', '-jar', simulator_file, simulator_path])
	results = read_output(output_file)

	norm_cars = float(results[0]) / n_cars  # the smaller the better
	norm_iter = float(results[1]) / n_simulator_iterations  # the higher the better

	fitness = ((1. - norm_cars) + norm_iter) / 2.  # the higher the better
	return fitness


def write_input(filename, **kwargs):
	"""
	Write input parameters for the simulator, such as number of iterations,
	cars and seed.
	:param filename: The input file.
	:param kwargs: Every parameter must be a named parameter. Currently there are
		three parameters, which may be provided IN THIS ORDER: iterations, cars, seed.
	"""

	with open(filename, 'wb') as input_file:
		input_file.write(str(kwargs['iterations']) + ' // iterações\n')
		input_file.write(str(kwargs['cars']) + ' // carros\n')
		input_file.write(str(kwargs['seed']) + ' // seed')


def write_times(filename, x):
	"""
	Writes the traffic lights times in a provided file.
	:param filename: The file which to write the times.
	:param x: The position of the particle being written.
	"""
	np.savetxt(filename, x, delimiter=',', fmt='%d')


def get_search_space_dimensions(filename):
	"""
	Given a file where the times for each traffic lights are assigned,
	returns the dimensionality of the search space.
	:param filename: The file where the times are stored.
	:rtype: tuple
	:return: A tuple where the first item is the number of crossroads and
		the second is the number of traffic lights for each crossroad.
	"""
	global_lines, global_columns = 0, 0
	with open(filename, 'rb') as times_file:
		for line in times_file:
			local_columns = len(line.strip(',').split(','))
			global_lines += 1
			if local_columns >= global_columns:
				global_columns = local_columns

	return global_lines, global_columns


def read_output(filename):
	"""
	Read the values in the output file, where the simulator has written.
	:param filename:
	:return:
	"""
	output = np.genfromtxt(filename, delimiter=',', comments='//', dtype=np.int)
	return output


def core(**kwargs):
	"""
	Core function for any PSO.
	:type update_function: function
	:param update_function: A function which will be used for updating the position of the particles.
	:type update_params: list
	:param update_params: a list of parameters that will be used for updating the position of the particles.

	:param n_particles: Number of particles in the swarm.
	:param n_iterations: Number of iterations for the PSO to run.
	:param n_cars: Number of cars in the simulation.
	:param n_simulator_iterations: Number of iterations in the simulation.
	:param seed: A seed which will be used both for the PSO and simulator.
	:param phi_1: The acceleration coefficient #1.
	:param phi_2: The acceleration coefficient #2.
	:param x_min: Lower border in the solution space for each axis.
	:param x_max: Upper border in the solution space for each axis.
	:param v_max: The maximum velocity which the particle cannot exceed.
	:param simulator_path: The path where the simulator is.
	:param input_file: File where the input parameters for the simulator,
		such as number of iterations, cars and seed, are stored.
	:param times_file: The file where the times for traffic lights are stored.
	:param output_file: The file where the simulator writes the results for each simulation.
	:param simulator_file: A pointer to the .jar simulator file.
	:param verbose: optional, defaults to True: Whether to display the progress of the PSO.
	:return: An N-dimensional array containing the solution for the simulation.
	"""

	np.random.seed(kwargs['seed'])
	random.seed(kwargs['seed'])

	write_input(kwargs['input_file'], **{'iterations': kwargs['n_simulator_iterations'], 'cars': kwargs['n_cars'], 'seed': kwargs['seed']})
	kwargs['n_crossroads'], kwargs['n_traffic_lights'] = get_search_space_dimensions(kwargs['times_file'])

	kwargs['x'] = np.random.randint(low=kwargs['x_min'], high=kwargs['x_max'], size=(kwargs['n_particles'], kwargs['n_crossroads'], kwargs['n_traffic_lights']))  # current position
	kwargs['p'] = kwargs['x'].copy()  # the individual best position so far
	kwargs['v'] = np.random.randint(low=-kwargs['v_max'], high=+kwargs['v_max'], size=(kwargs['n_particles'], kwargs['n_crossroads'], kwargs['n_traffic_lights']))  # the velocity
	kwargs['p_best'] = np.zeros(kwargs['n_particles'], dtype=np.float)  # the fitness of p
	kwargs['g'] = kwargs['v'][0]  # best individual in the population
	kwargs['g_best'] = kwargs['p_best'][0]

	for i in xrange(kwargs['n_iterations']):
		kwargs['p_current'] = swarm_fitness(kwargs['x'], kwargs['n_cars'], kwargs['n_simulator_iterations'], kwargs['times_file'], kwargs['simulator_file'], kwargs['output_file'], kwargs['simulator_path'])
		for j in xrange(kwargs['n_particles']):
			if kwargs['p_current'][j] > kwargs['p_best'][j]:
				kwargs['p_best'][j] = kwargs['p_current'][j]
				kwargs['p'][j] = kwargs['x'][j]  # replaces best position with current position

		k = np.argmax(kwargs['p_best'])
		if kwargs['p_best'][k] > kwargs['g_best']:
			kwargs['g'] = kwargs['p'][k]
			kwargs['g_best'] = kwargs['p_best'][k]

		if kwargs['verbose']:
			print 'iteration:', i, 'g_best:', kwargs['g_best'], 'p_best:', kwargs['p_best']

		if i + 1 < kwargs['n_iterations']:
			updated = kwargs['update_function'](**dict(itertools.izip(kwargs['update_params'], [kwargs[x] for x in kwargs['update_params']])))
			for (key, value) in updated.items():
				kwargs[key] = value

	k = np.argmax(kwargs['p_best'])
	if kwargs['verbose']:
		print 'k:', k, 'g_best:', kwargs['p_best'][k], 'x:'
		print kwargs['g']
	return kwargs['g']
