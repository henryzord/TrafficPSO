# coding=utf-8
import os
import numpy as np
import subprocess

__author__ = 'Henry'

# TODO three D-dimensional arrays: current position, previous best position, velocity

CONST_SIMULATOR_FILE = os.path.join('..', 'Simulator', 'dist', 'SwarmOptimization.jar')
CONST_INPUT_FILE = os.path.join('C:\\', 'temp', 'cars_app', 'input_params.txt')
CONST_OUTPUT_FILE = os.path.join('C:\\', 'temp', 'cars_app', 'output_params.txt')
CONST_TIMES_FILE = os.path.join('C:\\', 'temp', 'cars_app', 'times.txt')

CONST_N_INDIVIDUALS = 2

CONST_N_CARS = 200
CONST_N_ITERATIONS = 1000
CONST_SEED = 1
CONST_MAX_TIME = 10  # max time that a traffic light may be green (per traffic light)


def sample(p, max_time):
	"""
	Initial sample of the particle population.
	:param p: The search space where the particles will roam.
	:param max_time: Max time that a traffic light may be green.
	:return: The search space allocated from a uniform distribution.
	"""
	n_individuals, n_crossroads, n_lights = p.shape

	for i in xrange(n_individuals):
		p[i, :, :] = np.random.choice(max_time, size=n_lights * n_crossroads).reshape(n_crossroads, n_lights)

	return p


# TODO fitness must have two columns!
# TODO keep track of pbest fitness!

def update(p, pbest, v, g, phi_1, phi_2):
	n_individuals, n_crossroads, n_lights = p.shape

	for i in xrange(n_individuals):
		u_1 = np.random.randint(size=(n_crossroads, n_lights), low=0, high=phi_1)
		u_2 = np.random.randint(size=(n_crossroads, n_lights), low=0, high=phi_2)
		v[i] = v[i] + (u_1 * (p[i] - v[i])) + (u_2 * (pbest - p[i]))



def population_fitness(p):
	n_individuals, lines, columns = p.shape

	fitness = np.empty(n_individuals, dtype=np.float)
	for i in xrange(n_individuals):
		fitness[i] = individual_fitness(p[i])

	return fitness


def individual_fitness(individual):
	# writes times to times file
	write_times(CONST_TIMES_FILE, individual)
	subprocess.call(['java', '-jar', CONST_SIMULATOR_FILE])
	results = read_output(CONST_OUTPUT_FILE)
	fitness = fitness_function(results)
	return fitness


def fitness_function(results):
	norm_cars = float(results[0]) / CONST_N_CARS  # the smaller the better
	norm_iter = float(results[1]) / CONST_N_ITERATIONS  # the higher the better

	return ((1. - norm_cars) + norm_iter) / 2.  # the higher the better


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

	phi_1 = 1.
	phi_2 = 1.

	# current position
	x = np.random.randint(low=0, high=CONST_MAX_TIME, size=(CONST_N_INDIVIDUALS, lines, columns))
	# the individual best position so far
	p = x.copy()
	# the velocity
	v = np.random.randint(low=0, high=CONST_MAX_TIME, size=(CONST_N_INDIVIDUALS, lines, columns))
	# the fitness
	pbest = np.zeros(CONST_N_INDIVIDUALS, dtype=np.float)
	g = v[0]

	for i in xrange(CONST_N_ITERATIONS):
		fitness = population_fitness(p)
		for j in xrange(CONST_N_INDIVIDUALS):
			if fitness[j] > pbest[j]:
				pbest[j] = fitness[j]
				v[j] = x[j]
		g = v[np.argmax(pbest)]

		update(p, pbest, v, g, phi_1, phi_2)

if __name__ == '__main__':
	main()
