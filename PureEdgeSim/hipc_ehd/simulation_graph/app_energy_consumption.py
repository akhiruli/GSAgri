import matplotlib.pyplot as plt

x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
y_axis_GSAgri = [0, 5.69, 10.98, 15.02, 21.05, 23.23, 28.99, 32.55, 38.39, 44.09, 50.50]
y_axis_SCOPE = [0, 17.455, 35.87, 41.455, 62.04, 71.77, 83.62, 93.73, 109.94, 131.581, 144.5235]
y_axis_OCO = [0, 17.3, 34.305, 42.815, 67.095, 67.85, 76.46, 88.755, 100.89, 131.405, 134.405]
y_axis_RG = [0, 15.755, 26.985, 33.075, 53.935, 53.485, 68.83, 70.89, 86.5, 104.165, 112.155]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')
#plt.title('UE Energy Consumption', fontsize = 15)

plt.ylabel('Energy Consumption at UD (Wh)', fontsize = 15)
plt.xlabel('No. of Applications', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('app_energy_consumption.png', bbox_inches='tight')
plt.show()
