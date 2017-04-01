import signal
from subprocess import Popen, PIPE, STDOUT

class GameRunner:
    proc = None
    java_in = None
    java_out = None
    java_err = None
    num_iter = 0

    def __init__(self):
        self.spin_up()

    def spin_up(self):
        self.proc = Popen(['java', 'GameRunner'], stdin=PIPE, stdout=PIPE, stderr=PIPE)

    def one_iter(self, args):
        self.num_iter = self.num_iter+1;
        if (self.num_iter % 100 == 0):
            print('Iteration: {}'.format(self.num_iter))
        arg_string = " ".join(args)

        self.proc.stdin.write(arg_string.encode('utf-8'))
        self.proc.stdin.flush()

        # Wait for result
        result = float(self.proc.stdout.readline())

        return result

    def run(self, weights, display=False):
        args = [str(weights[i]) for i in range(0, 11)]
        return self.one_iter(args)

    def stop(self):
        Popen.send_signal(signal.SIGINT)
