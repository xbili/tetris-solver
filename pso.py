from pyswarm import pso
from game_runner import one_iter

# x is a vector of features
def utility(x):
    # Convert all arguments to string
    args = [str(arg) for arg in x]

    # Lines cleared
    cleared = one_iter(args)
    print('Lines cleared: {}'.format(cleared))

    return cleared

def con(x):
    x1 = x[0]
    x2 = x[1]
    return [-(x1 + 0.25)**2 + 0.75*x2]

def main():
    # Lower bound
    lb = [-100] * 21

    # Upper bound
    ub = [100] * 21

    xopt, fopt = pso(utility, lb, ub, f_ieqcons=con)

    print(xopt)
    print(fopt)

if __name__ == '__main__':
    main()
