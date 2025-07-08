import matplotlib.pyplot as plt

x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
y_axis_GSAgri = [0, 17, 22, 30, 34, 35, 48, 53, 46, 61, 47]
y_axis_SCOPE = [0, 1, 10, 7, 10, 12, 19, 24, 22, 28, 27]
y_axis_OCO = [0, 4, 12, 11, 14, 22, 29, 33, 32, 42, 35]
y_axis_RG = [0, 11, 15, 24, 35, 38, 40, 43, 44, 45, 49]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')

#plt.title('Average Latency', fontsize = 15)

plt.ylabel('Number of tasks in nearest DC', fontsize = 15)
plt.xlabel('No. of Applications', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('high_sec_mec.png', bbox_inches='tight')
plt.show()
