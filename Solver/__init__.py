# coding=utf-8
from Solver import Inertial

__author__ = 'Henry'

import os
import Original
import numpy as np


def main():
	simulator_file = os.path.join('..', 'Simulator', 'dist', 'SwarmOptimization.jar')
	input_file = os.path.join('C:\\', 'temp', 'cars_app', 'input_params.txt')
	output_file = os.path.join('C:\\', 'temp', 'cars_app', 'output_params.txt')
	times_file = os.path.join('C:\\', 'temp', 'cars_app', 'times.txt')
	original_solution_file = os.path.join('C:\\', 'temp', 'cars_app', 'original_solution.txt')
	inertial_solution_file = os.path.join('C:\\', 'temp', 'cars_app', 'inertial_solution.txt')

	n_simulator_iterations = 100
	n_cars = 100
	seed = 1

	x_max = 10  # max time that a traffic light may be green (per traffic light)
	x_min = 1   # min time that a traffic light may be green (per traffic light)
	v_max = 2  # maximum velocity used in the updating of particles positions

	phi_1 = 2  # randomness factor #1
	phi_2 = 2  # randomness factor #2

	n_particles = 10
	n_iterations = 10

	omega = 0.9
	viscosity_factor = 0.05

	# original_solution = Original.pso(
	# 	n_particles=n_particles,
	# 	n_iterations=n_iterations,
	# 	n_cars=n_cars,
	# 	n_simulator_iterations=n_simulator_iterations,
	# 	seed=seed,
	# 	phi_1=phi_1,
	# 	phi_2=phi_2,
	# 	x_min=x_min,
	# 	x_max=x_max,
	# 	v_max=v_max,
	# 	input_file=input_file,
	# 	times_file=times_file,
	# 	output_file=output_file,
	# 	simulator_file=simulator_file,
	# 	verbose=True
	# )
	# np.savetxt(original_solution_file, original_solution, delimiter=',', fmt='%d')  # saves solution to file

	inertial_solution = Inertial.pso(
		n_particles=n_particles,
		n_iterations=n_iterations,
		n_cars=n_cars,
		n_simulator_iterations=n_simulator_iterations,
		seed=seed,
		omega=omega,
		viscosity_factor=viscosity_factor,
		phi_1=phi_1,
		phi_2=phi_2,
		x_min=x_min,
		x_max=x_max,
		v_max=v_max,
		input_file=input_file,
		times_file=times_file,
		output_file=output_file,
		simulator_file=simulator_file,
		verbose=True
	)
	np.savetxt(inertial_solution_file, inertial_solution, delimiter=',', fmt='%d')  # saves solution to file

if __name__ == '__main__':
	main()
