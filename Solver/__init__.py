# coding=utf-8
import os
import numpy as np
import subprocess

__author__ = 'Henry'

CONST_SIMULATOR_FILE = os.path.join('C:\\', 'Users', 'GPIN', 'HenryCodes', 'TrafficPSO', 'dist', 'SwarmOptimization.jar')
CONST_INPUT_FILE = os.path.join('C:\\', 'temp', 'cars_app', 'input_params.txt')
CONST_OUTPUT_FILE = os.path.join('C:\\', 'temp', 'cars_app', 'output_params.txt')
CONST_TIMES_FILE = os.path.join('C:\\', 'temp', 'cars_app', 'times.txt')

CONST_N_INDIVIDUALS = 2

CONST_N_CARS = 200
CONST_N_ITERATIONS = 1000
CONST_SEED = 1
CONST_MAX_TIME = 10  # max time that a traffic light may be green (per traffic light)


def sample(search_space, max_time):
	"""
	Initial sample of the particle population.
	:param search_space: The search space where the particles will roam.
	:param max_time: Max time that a traffic light may be green.
	:return: The search space allocated from a uniform distribution.
	"""
	n_individuals, n_crossroads, n_lights = search_space.shape

	for i in xrange(n_individuals):
		search_space[i, :, :] = np.random.choice(max_time, size=n_lights * n_crossroads).reshape(n_crossroads, n_lights)

	return search_space


def population_fitness(search_space):
	n_individuals, lines, columns = search_space.shape

	fitness = np.empty(n_individuals, dtype=np.float)
	for i in xrange(n_individuals):
		fitness[i] = individual_fitness(search_space[i])

	return fitness


def individual_fitness(individual):
	# writes times to times file
	write_times(CONST_TIMES_FILE, individual)
	subprocess.call(['java', '-jar', CONST_SIMULATOR_FILE])
	results = read_output(CONST_OUTPUT_FILE)
	fitness = fitness_function(results)
	return fitness


def fitness_function(results):
	norm_cars = results[0] / CONST_N_CARS  # the smaller the better
	norm_iter = results[1] / CONST_N_ITERATIONS  # the smaller the better

	return 1. -((norm_cars + norm_iter) / 2.)


def write_input(filename, **kwargs):
	with open(filename, 'wb') as input_file:
		input_file.write(str(kwargs['iterations']) + ' // iterações\n')
		input_file.write(str(kwargs['cars']) + ' // carros\n')
		input_file.write(str(kwargs['seed']) + '// seed')


def write_times(filename, individual):
	np.savetxt(filename, individual, delimiter=',', fmt='%d')


def read_times(filename):
	global_lines, global_columns = 0, 0
	with open(filename, 'rb') as times_file:
		for line in times_file:
			local_columns = len(line.strip(',').split(','))
			global_lines += 1
			if local_columns >= global_columns:
				global_columns = local_columns

	return global_lines, global_columns


def read_output(filename):
	output = np.genfromtxt(filename, delimiter=',', comments='//', dtype=np.int)
	return output


def main():
	write_input(CONST_INPUT_FILE, **{'iterations': CONST_N_ITERATIONS, 'cars': CONST_N_CARS, 'seed': CONST_SEED})
	lines, columns = read_times(CONST_TIMES_FILE)
	search_space = sample(
		np.empty((CONST_N_INDIVIDUALS, lines, columns), dtype=np.int),
		CONST_MAX_TIME
	)

	fitness = population_fitness(search_space)

	# write_times(times_file, times=times)

if __name__ == '__main__':
	main()
