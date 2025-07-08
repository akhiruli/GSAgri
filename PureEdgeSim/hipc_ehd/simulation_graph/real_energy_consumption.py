import matplotlib.pyplot as plt

#x_axis = [0, 70, 140, 210, 280, 350, 420, 490, 560, 630, 700]
x_axis = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

y_axis_GSAgri = [0, 1.27, 2.57, 3.85, 5.12, 6.387, 7.99, 9.031, 10.19, 11.75, 12.72]
y_axis_SCOPE = [0, 2.99, 5.13, 7.69, 10.71, 11.77, 14.93, 19.42, 22.87, 25.41, 29.13]
y_axis_RG = [0, 2.24, 4.126, 6.62, 9.36, 12.99, 14.66, 18.52, 24.07, 27.63, 31.15]
y_axis_OCO = [0, 1.58, 3.087, 4.706, 6.19, 7.55, 9.44, 10.97,12.56, 13.86, 15.62]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')
#plt.title('UE Energy Consumption', fontsize = 15)

plt.ylabel('Energy Consumption at IoT (In Wh)', fontsize = 15)
plt.xlabel('No. of Applications', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('real_energy_consumption.png', bbox_inches='tight')
plt.show()
