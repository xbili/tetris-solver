from bayes_opt import BayesianOptimization
import random
from game_runner import GameRunner
gr = GameRunner()

def game_runner_evaluate(
						# col1, col2, col3, col4, col5, col6, col7, col8, col9, col10,
						h_diff1, h_diff2, h_diff3, h_diff4, h_diff5, h_diff6, h_diff7, h_diff8, h_diff9,
						highest_col, num_holes, num_holes_made, num_lines_cleared):
	params = []
	# params.extend([col1, col2, col3, col4, col5, col6, col7, col8, col9, col10])
	params.extend([h_diff1, h_diff2, h_diff3, h_diff4, h_diff5, h_diff6, h_diff7, h_diff8, h_diff9])
	params.extend([highest_col])
	params.extend([num_holes])
	params.extend([num_holes_made])
	params.extend([num_lines_cleared])
	to_display = True
	# for num in params:
	# 	print(num)

	lines_cleared = gr.run(params, display=to_display)
	return lines_cleared

grBO = BayesianOptimization(game_runner_evaluate,{
													# 'col1': (-100,100),
												 #  'col2': (-100,100),
												 #  'col3': (-100,100),
												 #  'col4': (-100,100),
												 #  'col5': (-100,100),
												 #  'col6': (-100,100),
												 #  'col7': (-100,100),
												 #  'col8': (-100,100),
												 #  'col9': (-100,100),
												 #  'col10': (-100,100),
												  'h_diff1': (-100,100),
												  'h_diff2': (-100,100),
												  'h_diff3': (-100,100),
												  'h_diff4': (-100,100),
												  'h_diff5': (-100,100),
												  'h_diff6': (-100,100),
												  'h_diff7': (-100,100),
												  'h_diff8': (-100,100),
												  'h_diff9': (-100,100),
												  'highest_col': (-100,100),
												  'num_holes': (-100,100),
												  'num_holes_made': (-100,100),
												  'num_lines_cleared': (-100,100),
												  })
grBO.maximize(init_points = 5,  n_iter=15)

# game_runner_evaluate(1,2,3,4,5,100,7,8,9,10,1,2,3,4,5,6,7,8,9,19)