import signal
from subprocess import Popen, PIPE, STDOUT

# from jnius import autoclass
# GameRunner = autoclass('GameRunner')
class GameRunner:
    proc = None
    java_in = None
    java_out = None
    java_err = None
    num_iter = 0
    # One run of the game
    def spin_up(self):
        # GameRunner.main(args)
        self.proc = Popen(['java', 'GameRunner'], stdin=PIPE, stdout=PIPE, stderr=PIPE)


    def one_iter(self, args):
        self.num_iter = self.num_iter+1;
        if (self.num_iter % 100 == 0):
            print(self.num_iter)
        arg_string = " ".join(args)
        # print(arg_string)
        # print('waiting for args')
        self.proc.stdin.write(arg_string.encode('utf-8'))
        self.proc.stdin.flush()
        # print(bytes(arg_string, 'utf-8'), file=self.proc.stdin)
        # print('returning result')
        result = float(self.proc.stdout.readline()) # this will wait for result
        # print(result)
        return result


    # Runs the game for n number of times with update rule
    # args: initial weights
    # update: how do you want to update your weights given result from
    #    one iteration (higher order function)
    def run_game(self, n, args, update):
        while n > 0:
            result = int(one_iter(args))

            args = update(args, result)
            n -= 1

        return args

    def __init__(self):
        self.spin_up()
        self.num_iter = 0

    def run(self, weights, display=False):
        args = [str(weights[i]) for i in range(0, 11)]
        return self.one_iter(args)

    def stop(self):
        Popen.send_signal(signal.SIGINT)

    if __name__ == '__main__':
        # This list should be our weights
        args = [str(0.01) for i in range(0, 11)]
        run(args)
        # def mock_update_rule(args, result):
        #     return [arg for arg in args]
        #
        # # Convert back to float
        # result = [float(arg) for arg in run_game(10, args, mock_update_rule)]
        # print(result)
