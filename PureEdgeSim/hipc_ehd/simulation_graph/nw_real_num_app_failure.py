import matplotlib.pyplot as plt

x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

y_axis_GSAgri = [0, 1, 4, 5, 6, 7, 6, 8, 9, 11, 15]
y_axis_SCOPE = [0, 1, 4, 6, 10, 16, 19, 24, 31, 42, 45]
y_axis_OCO = [0, 1, 4, 8, 11, 15, 21, 26, 33, 45, 49]
y_axis_RG = [0, 1, 4, 9, 17, 29, 45, 48, 55, 58, 64]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Task Failure Rate', fontsize = 15)

plt.ylabel('Number of App failed', fontsize = 16)
plt.xlabel('No. of Applications', fontsize = 16)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('nw_real_num_app_failure.png', bbox_inches='tight')
plt.show()
