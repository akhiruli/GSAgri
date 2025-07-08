import matplotlib.pyplot as plt

x_axis = [0, 481, 903, 1314, 1746, 2194, 2629, 2973, 3387, 3855, 4294]

#failure:
y_axis_task_failure_pred = [0, 1.871, 1.882, 7.229, 4.467, 8.66, 8.101, 7.702, 8.148, 9.208, 8.197]
y_axis_task_failure_non_pred = [0, 2.702, 3.661, 7.686, 6.841, 9.161, 9.128, 8.572, 9.52, 9.961, 10.012]

#Failure on Energy
y_axis_task_failure_energy_const_pred = [0, 0, 0, 17, 1,  18, 21, 16, 11, 60, 22]
y_axis_task_failure_energy_const_non_pred = [0, 0, 0, 22, 8, 24, 14, 27, 14, 71, 27]

#energy consumption
#y_axis_energy_consumption_pred=[0, 26.8063, 52.038, 72.624, 103.923, 119.595, 146.924, 165.550, 192.324, 233.336, 251.789]
#y_axis_energy_consumption_non_pred = [0, 29.9024, 57.9701, 70.9023, 114.7352, 125.8541, 154.4443, 175.5405, 208.2505, 245.4393, 281.6943]

#fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(6, 8), sharex=True) #horizontal
fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(6, 6), sharex=True) #horizontal

ax1.plot(x_axis, y_axis_task_failure_pred, marker='x', label='Without Energy Prediction', linewidth=1, color='green')
ax1.plot(x_axis, y_axis_task_failure_non_pred, marker='o', label='With Energy Prediction', linewidth=1, color='blue')
ax1.set_ylabel('Task failure rate (%)', fontsize = 12)

ax2.plot(x_axis, y_axis_task_failure_energy_const_pred,  marker='x', label='Without Energy Prediction', linewidth=1, color='green')
ax2.plot(x_axis, y_axis_task_failure_energy_const_non_pred, marker='o', label='With Energy Prediction', linewidth=1, color='blue')
ax2.set_ylabel('No of task failed on \n energy constraint', fontsize = 12)

#ax3.plot(x_axis, y_axis_energy_consumption_pred,  marker='x', label='Without Energy Prediction', linewidth=1, color='green')
#ax3.plot(x_axis, y_axis_energy_consumption_non_pred, marker='o', label='With Energy Prediction', linewidth=1, color='blue')
#ax3.set_ylabel('Energy consumption (Wh)', fontsize = 12)

plt.xlabel('No. of Tasks', fontsize = 13) 
ax1.legend(loc='best', prop={'size': 12})
ax2.legend(loc='best', prop={'size': 12})
#ax3.legend(loc='best', prop={'size': 12})

plt.savefig('LSTM_random_EH.png', bbox_inches='tight')
plt.show()
