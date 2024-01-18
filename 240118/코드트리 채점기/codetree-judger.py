from queue import PriorityQueue
from collections import deque

waiting=PriorityQueue() # 채점 대기 큐
estimator=[] # 채점 중인 채점기 (채점 중이면 true)
judge={ } # 채점 중인 태스크 {도메인:(t_start, u, j)}
history={ } # 채점 완료 스택 {도메인:[(t_start, t_end, u, j), ...,]}

def push_waiting(t, p, u):
    item=(p, (t, u))
    waiting.put(item)
    # print(waiting.get()) 

def init(N, u0): # (1, (0, 'codetree.ai/16'))
    global estimator
    estimator=[True for _ in range(N+1)]
    push_waiting(0, 1, u0)

def check_dup_url(u): ###
    # 완전히 일치하는 url이 대기큐에 존재
    for item in waiting.queue:
        if item[1][1]==u:
            return True
    return False

def request_judging(t, p, u):
    if check_dup_url(u)==False:
        push_waiting(t, p, u)

def gap_appropriate(domain, t):
    if domain in history:
        recent_task=history[domain][-1]
        # print('현재', recent_task)
        s=recent_task[0]
        e=recent_task[1]
        if t<3*e-2*s:
            return False
    return True
    
def check_not_now(domain, time):
    # 채점을 진행중인 도메인이면 조건 미달
    if domain in judge:
        if judge[domain]==True:
            # print('채점 중인 도메인')
            return 0
    # gap 조건 미달
    if not gap_appropriate(domain, time):
        # print('gap이 올바르지 않음')
        return 0
    # 채점기 선택해서 할당
    for i in range(1, len(estimator)):
        if estimator[i] is True:
            estimator[i]=False # 채점 중인 상태!
            # print(f"채점기 선택{i}")
            return i
    return 0

def start_judging(t):
    # 대기큐에서 우선순위인 task선택
    task=waiting.get()
    # 즉시 채점 가능한지 확인 후 채점기 할당
    domain=task[1][1].split('/')[0]
    j_id=check_not_now(domain, t)
    if j_id != 0: # 채점
        judge[domain]=(t, task[1][1], j_id)
    else: # 대기
        push_waiting(task[1][0], task[0], task[1][1])

def end_judging(t, j):
    if estimator[j] is False:
        estimator[j]=True
        task_init = next(((key, value) for key, value in judge.items() if value[2] == j), None)
        if task_init is None:
            return
        # print('task_init', task_init)
        task=(task_init[1][0], t, task_init[1][1], task_init[1][2])
        if task_init[0] not in history:
            history[task_init[0]]=deque([task])
        else:
            history[task_init[0]].append(task)
        # print('history :', history)


def select_waiting():
    print(waiting.qsize()) 

# main
q=int(input())
for i in range(1,q+1):
    query=input().split()
    # print(f'명령어 {i}번째: {query}')

    if query[0]=='100': # 코드트리 채점기 준비
        init(int(query[1]), query[2])
    elif query[0]=='200': # 채점 요청: 대기 큐 추가 or not
        request_judging(int(query[1]), int(query[2]), query[3])
    elif query[0]=='300': # 채점 시도: 채점으로 이동 or not
        start_judging(int(query[1]))
    elif query[0]=='400': # 채점 종료: history로 이동
        end_judging(int(query[1]), int(query[2]))
    elif query[0]=='500': # 채점 대기 큐 내 개수 조회
        # print('---------------------')
        select_waiting()

    # 대기 큐 출력
    # for item in waiting.queue:
        #print(item)
    #print()