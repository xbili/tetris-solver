import sys
from subprocess import Popen, PIPE

# from jnius import autoclass
# GameRunner = autoclass('GameRunner')

# One run of the game
def one_iter(args):
    # GameRunner.main(args)
    with Popen(['java', 'GameRunner', *args], stdout=PIPE) as proc:
        return proc.stdout.read()

# Runs the game for n number of times with update rule
# args: initial weights
# update: how do you want to update your weights given result from
#    one iteration (higher order function)
def run_game(n, args, update):
    while n > 0:
        result = int(one_iter(args))

        args = update(args, result)
        n -= 1

    return args

def run(weights, display=False):
    args = [str(weights[i]) for i in range(0, 21)]
    result = int(one_iter(args))
    print(result)
    return result

if __name__ == '__main__':
    # This list should be our weights
    args = [str(0.01) for i in range(0, 21)]
    run(args)
    # def mock_update_rule(args, result):
    #     return [arg for arg in args]
    #
    # # Convert back to float
    # result = [float(arg) for arg in run_game(10, args, mock_update_rule)]
    # print(result)
