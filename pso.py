from pyswarm import pso
from game_runner import GameRunner

class PSOLearner(object):

    # Static constants
    UPPER_BOUND = 10
    LOWER_BOUND = -10
    OMEGA = 0.75
    SWARMSIZE = 500
    MAX_ITER = 10000
    SHOW_DEBUG = False

    def __init__(self):
        self.runner = GameRunner()
        self.min_cleared = 0

    # x is a vector of features
    def utility(self, x):
        # Convert all arguments to string
        args = [str(arg) for arg in x]

        # Lines cleared
        cleared = self.runner.run(args)
        if int(float(cleared)) > self.min_cleared:
            print(args)
            print(cleared)
            self.min_cleared = cleared

        # Return negative because we want to maximize this value
        return -cleared

    def learn(self):
        # Lower bound
        lb = [self.LOWER_BOUND] * 11

        # Upper bound
        ub = [self.UPPER_BOUND] * 11

        xopt, fopt = pso(self.utility, lb, ub,
                         omega=self.OMEGA, swarmsize=self.SWARMSIZE,
                         maxiter=self.MAX_ITER, debug=self.SHOW_DEBUG)

        print(xopt)
        print(fopt)

if __name__ == '__main__':
    learner = PSOLearner()
    learner.learn()
