# coding=utf-8

__author__ = 'Henry'

import numpy as np
from Solver.__canonical__ import core


def update(x, p, v, g, viscosity_factor, omega, phi_1, phi_2, x_min, x_max, v_max):
	"""
	Updates the particles position based on velocities.
	:param x: An array where each entry is the position of the i-th particle.
	:param p: An array where each entry is the best past position of the i-th particle.
	:param v: An array where each entry is the velocity of the i-th particle.
	:param g: The best particle so far.
	:param viscosity_factor: the increment for omega.
	:param omega: The viscosity coefficient.
	:param phi_1: The acceleration coefficient #1.
	:param phi_2: The acceleration coefficient #2.
	:param x_min: Lower border in the solution space for each axis.
	:param x_max: Upper border in the solution space for each axis.
	:param v_max: The maximum velocity which the particle cannot exceed.

	:rtype: dict
	:return: A dictionary where the keys are the names of variables updated, and the value
		is the updated value by this function.
	"""
	n_individuals, n_crossroads, n_lights = p.shape

	for i in xrange(n_individuals):
		u_1 = np.random.randint(size=(n_crossroads, n_lights), low=0, high=+phi_1)
		u_2 = np.random.randint(size=(n_crossroads, n_lights), low=0, high=+phi_2)
		v[i] = np.clip(
			a=((omega * v[i]) + (u_1 * (p[i] - x[i])) + (u_2 * (g - x[i]))),
			a_min=-v_max,
			a_max=+v_max
		)
		x[i] = np.clip(
			a=(x[i] + v[i]),
			a_min=x_min, a_max=x_max
		)  # clips values so it won't exceed border values

	omega += viscosity_factor

	return {'omega': omega, 'x': x, 'v': v}


def pso(**kwargs):
	"""
	Main method for the inertial PSO.
	:param n_particles: Number of particles in the swarm.
	:param n_iterations: Number of iterations for the PSO to run.
	:param n_cars: Number of cars in the simulation.
	:param n_simulator_iterations: Number of iterations in the simulation.
	:param seed: A seed which will be used both for the PSO and simulator.
	:param omega: The viscosity coefficient, which will be decreased over time.
	:param viscosity_factor: the increment for omega.
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
	kwargs['update_function'] = update
	kwargs['update_params'] = ['x', 'p', 'v', 'g', 'viscosity_factor', 'omega', 'phi_1', 'phi_2', 'x_min', 'x_max', 'v_max']

	return core(**kwargs)
