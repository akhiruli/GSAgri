import matplotlib.pyplot as plt

x_axis = [0, 481, 903, 1314, 1746, 2194, 2629, 2973, 3387, 3855, 4294]
y_axis_GSAgri = [0, 26.8063, 52.038, 72.624, 103.923, 119.595, 146.924, 165.550, 192.324, 233.336, 251.789]
y_axis_SCOPE = [0, 57.108, 121.313, 134.187, 202.307, 231.803, 280.002, 323.518, 395.60, 407.537, 475.142]
y_axis_RG = [0, 56.653, 98.391, 138.484, 181.052, 209.140, 236.985, 284.114, 350.934, 388.1211, 421.530]
y_axis_OCO = [0, 41.709, 97.7342, 111.235, 148.208, 180.734, 227.781, 266.833, 284.719, 341.743, 386.202]

plt.plot(x_axis, y_axis_GSAgri, marker='x', label='GSAgri', linewidth=1.3, color='green')
plt.plot(x_axis, y_axis_SCOPE, marker='o', label='SCOPE', linewidth=1.3, color='blue')
plt.plot(x_axis, y_axis_RG, marker='v', label='RG', linewidth=1.3, color='orange')
plt.plot(x_axis, y_axis_OCO, marker='D', label='OCO', linewidth=1.3, color='m')
#plt.title('UE Energy Consumption', fontsize = 15)

plt.ylabel('Energy Consumption at IoT (In Wh)', fontsize = 15)
plt.xlabel('No. of Tasks', fontsize = 15)
plt.legend(loc='best', prop={'size': 15})
plt.savefig('energy_consumption.png', bbox_inches='tight')
plt.show()
