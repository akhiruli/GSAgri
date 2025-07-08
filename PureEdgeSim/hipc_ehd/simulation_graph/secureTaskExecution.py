
import numpy as np  
import matplotlib.pyplot as plt  

barWidth = 0.15
fig = plt.subplots(figsize =(8, 6))

X = ['UE','Nearest DC','Others']
GSAgri=[70, 1151, 273]
SCOPE=[224, 194, 1045]
RG=[207, 1014, 206]
OCO=[147, 284, 1021]

br1 = np.arange(len(GSAgri))
br2 = [x + barWidth for x in br1]
br3 = [x + barWidth for x in br2]
br4 = [x + barWidth for x in br3]

plt.bar(br1, GSAgri, width = barWidth,
        color ='g', edgecolor ='grey', label ='GSAgri')
plt.bar(br2, SCOPE,  width = barWidth,
        color ='b', edgecolor ='grey', label ='SCOPE')
plt.bar(br3, RG, width = barWidth,
        color ='orange', edgecolor ='grey', label ='RG')
plt.bar(br4, OCO, width = barWidth,
        color ='m', edgecolor ='grey', label ='OCO')

#plt.xlabel('Task Mix', fontweight ='bold', fontsize = 15)
plt.ylabel('High Security Requirement Tasks', fontsize = 15)
plt.xticks([r + barWidth/2 for r in range(len(GSAgri))],
        ['UE', 'Nearest DC', 'Others'], fontsize = 15)

plt.legend()
plt.savefig('high_sec.png', bbox_inches='tight')
plt.show()
