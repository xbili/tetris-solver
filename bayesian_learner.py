from bayes_opt import BayesianOptimization
import random
from game_runner import GameRunner
gr = GameRunner()

def game_runner_evaluate(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10,
						h_diff1, h_diff2, h_diff3, h_diff4, h_diff5, h_diff6, h_diff7, h_diff8, h_diff9,
						num_holes):
	params = []
	params.extend([col1, col2, col3, col4, col5, col6, col7, col8, col9, col10,
						h_diff1, h_diff2, h_diff3, h_diff4, h_diff5, h_diff6, h_diff7, h_diff8, h_diff9])
	params.extend([max(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10)])
	params.extend([num_holes])
	to_display = True
	# for num in params:
	# 	print(num)

	lines_cleared = gr.run(params, display=to_display)
	return lines_cleared

grBO = BayesianOptimization(game_runner_evaluate,{'col1': (0,1),
												  'col2': (0,1),
												  'col3': (0,1),
												  'col4': (0,1),
												  'col5': (0,1),
												  'col6': (0,1),
												  'col7': (0,1),
												  'col8': (0,1),
												  'col9': (0,1),
												  'col10': (0,1),
												  'h_diff1': (0,1),
												  'h_diff2': (0,1),
												  'h_diff3': (0,1),
												  'h_diff4': (0,1),
												  'h_diff5': (0,1),
												  'h_diff6': (0,1),
												  'h_diff7': (0,1),
												  'h_diff8': (0,1),
												  'h_diff9': (0,1),
												  'num_holes': (0,1),
												  })
grBO.maximize(init_points = 5,  n_iter=15)

# game_runner_evaluate(1,2,3,4,5,100,7,8,9,10,1,2,3,4,5,6,7,8,9,19)