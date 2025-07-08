import matplotlib.pyplot as plt

DCs = ['DC1', 'DC2', 'DC3']
values = [822.8/1852.1, 482.7/1852.1, 546.6/1852.1]
colors = ['g', 'orange', 'm']

#plt.pie(sizes, labels=labels, colors=colors, autopct='%1.1f%%', shadow=True)
plt.pie(values, labels=DCs, colors=colors, autopct='%1.1f%%')
plt.axis('equal')
#plt.title("Pie Chart with Custom Colors")
plt.savefig('task_dist_edge_dc.png', bbox_inches='tight')
plt.show()
