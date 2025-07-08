import matplotlib.pyplot as plt

x_axis = [0, 481, 903, 1314, 1746, 2194, 2629, 2973, 3387, 3855, 4294]

y_axis_GSAgri = [0, 1.871, 1.882, 7.229, 4.467, 8.66, 8.101, 7.702, 8.148, 9.208, 8.197]
y_axis_SCOPE = [0, 11.850, 13.289, 17.427, 18.098, 19.097, 20.349, 19.408, 20.992, 21.608, 20.726]
y_axis_RG = [0, 12.681, 12.956, 14.992, 14.089, 15.451, 20.464, 22.267, 17.153, 24.020, 24.592]
y_axis_OCO = [0, 10.81, 16.832, 16.971, 17.984, 14.038, 18.372, 16.481, 14.260, 19.61, 19.329]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Task Failure Rate', fontsize = 15)

plt.ylabel('Task Failure rate (%)', fontsize = 16)
plt.xlabel('No. of Tasks', fontsize = 16)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('task_failure_rate.png', bbox_inches='tight')
plt.show()
