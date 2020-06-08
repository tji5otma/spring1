#!/usr/bin/env python
# coding: utf-8

# In[3]:


import pandas as pd
import os
import numpy as np
import matplotlib.pyplot as plt
    


# In[2]:


import sys

args = sys.argv


# In[4]:


def plot_status(sprint_name, team):
    path = "./" + team + "/"
    
    files = sorted(os.listdir(path))
    
    tmp_df = None

    for f in files:

        df = pd.read_csv(path+f)
        tmp_se = df["ステータス"].value_counts()

        if tmp_df is None:
            tmp_df = tmp_se
        else:
            tmp_df = pd.concat([tmp_df, tmp_se], axis=1)

    tmp_df = tmp_df.T

    tmp_df.index = np.array(range(len(tmp_df)))+1

    status_dict = {' ToDo':'ToDo', ' 進行中':'Doing', ' レビュー中':'Reviewing', ' 昨日完了':'Done Recently', ' 完了':'Done'}

    # カラム名を英語に置換
    tmp_df.columns = tmp_df.columns.map(status_dict)

    # nanをゼロに置換
    tmp_df = tmp_df.fillna(0)
    
    # 列順の入れ替え
    tmp_df = tmp_df[["ToDo","Doing","Reviewing","Done Recently","Done"]]    

    # 描写
    fig = plt.figure()

    tmp_df.plot.area(title=sprint_name+" "+team,
            grid=True,
            colormap='jet',
            alpha=0.8)

    plt.xlabel("day")
    plt.ylabel("ticket")

    plt.savefig('./'+ sprint_name+' '+team+'.png')
    plt.close('all')
    
    print("Success")


    


# In[ ]:


if __name__ == '__main__':
    
    args = sys.argv

    if len(args)==3:

        plot_status(args[1], args[2])

    else:
        print('Arguments do not match')


# In[ ]:





# In[177]:




