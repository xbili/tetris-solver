from pyswarm import pso

# x is a vector of features
def utility(x):
    return sum(x)

def con(x):
    x1 = x[0]
    x2 = x[1]
    return [-(x1 + 0.25)**2 + 0.75*x2]

def main():
    lb = [-3, -1]
    ub = [2, 6]

    xopt, fopt = pso(utility, lb, ub, f_ieqcons=con)

    print(xopt)
    print(fopt)

