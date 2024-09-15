info = list(input().split())
L = int(info[0])
Q = int(info[1])

client = {}  # {'june': (7, 4, 2), ..., 'name': (t,x,n)}
sushi_num = 0
people_num = 0

### 입력받아 cmd_list에 저장
cmd_list = []
for _ in range(Q):
    command = list(input().split())
    cmd_list.append(command)

    if command[0] =='200':
        name = command[3]
        t = int(command[1])
        x1 = int(command[2])
        n = int(command[4])
        client[name] = [t, x1, n]


### 소멸 쿼리 cmd_list_plus에 생성
cmd_list_plus=[]
for cmd in cmd_list: 
    if cmd[0] == '100':  # 스시 추가 & 소멸 쿼리 생성
        t = int(cmd[1])
        x0 = int(cmd[2])
        name = cmd[3]

        x1 = client[name][1]
        t1 = client[name][0]
        if x0 <= x1:  # 손님이 앞에 있음
            t_del = x1-x0+t
        else:    # 손님이 원 인덱스 넘어감
            t_del = L-x0+x1+t
        if t1 > t_del:  # 소멸 예정시간에 손님이 없는 경우
            t_del += ((t1 - t_del + L-1) // L) * L
        cmd_list_plus.append(['111', str(t_del), name])

### 쿼리 합친 후 정렬
cmd_list += cmd_list_plus
cmd_list = sorted(cmd_list, key=lambda x: (int(x[1]), x[0]))


### 카운팅 수행
for cmd in cmd_list:
    if cmd[0] == '100':  # 스시 추가 & 소멸 쿼리 생성
        sushi_num += 1

    elif cmd[0] == '111':  # 스시 삭제
        sushi_num -= 1

        name = cmd[2] # 클라이언트 삭제
        client[name][2] -= 1  # n--
        if client[name][2] == 0:
            people_num -= 1

    elif cmd[0] == '200':  # 클라이언트 추가
        people_num += 1

    elif cmd[0] == '300':  # 사진 촬영
        print(people_num, sushi_num)