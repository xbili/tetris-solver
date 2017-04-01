from pyswarm import pso
from game_runner import GameRunner

class PSOLearner(object):

    runner = GameRunner()
    min_cleared = 0

    # x is a vector of features
    def utility(self, x):
        # Convert all arguments to string
        args = [str(arg) for arg in x]

        # Lines cleared
        # print(args)
        cleared = self.runner.run(args)
        if int(float(cleared)) > self.min_cleared:
            print(args)
            print(cleared)

        return -cleared

    def learn(self):
        # Lower bound
        lb = [-100] * 13

        # Upper bound
        ub = [100] * 13

        xopt, fopt = pso(self.utility, lb, ub,
                         maxiter=10000)

        print(xopt)
        print(fopt)

if __name__ == '__main__':
    learner = PSOLearner()
    learner.learn()

