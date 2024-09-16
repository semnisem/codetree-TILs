### 게임 변수 입력 및 초기 세팅
N, M, P, C, D = list(map(int, input().split()))

board = [[-1 for _ in range(N+1)] for _ in range(N+1)]  # NxN 크기의 게임판이자 산타, 루돌프의 위치
santa_scores = [0 for _ in range(P+1)]  # 1번~P번 산타의 점수
santa_okays = ["ok" for _ in range(P+1)]  # 1번~P번 산타의 상태
santa_distances = [2*N**2 for _ in range(P+1)]  # 1번~P번 산타와의 거리

# 루돌프 입력
roo_init = list(map(int, input().split()))
roo = [roo_init[0], roo_init[1]]
board[roo_init[0]][roo_init[1]] = 0  # 보드에 0

# 산타 입력
for santa in range(1, P+1):
    i, r, c = list(map(int, input().split()))
    board[r][c] = i  # 보드에 산타번호

def is_okay(santa): # 기절도 아니고 탈락도 아닌 산타
    global santa_okays
    if santa_okays[santa] == "ok":
        return True
    return False


def get_distance(p):
    global santa_distances
    global roo

    roo_r, roo_c = roo
    santa_r, santa_c = None, None
    for r in range(1, N+1):
        for c in range(1, N+1):
            if board[r][c] == p:
                santa_r, santa_c = r, c
                break  # 값을 찾았으므로 내부 루프 종료
        if santa_r is not None:
            break  # 값을 찾았으므로 외부 루프 종료

    return (roo_r-santa_r)**2+(roo_c-santa_c)**2

def get_location(p):
    santa_r, santa_c = None, None
    for r in range(1, N + 1):
        for c in range(1, N + 1):
            if board[r][c] == p:
                santa_r, santa_c = r, c
                break  # 값을 찾았으므로 내부 루프 종료
        if santa_r is not None:
            break  # 값을 찾았으므로 외부 루프 종료
    return santa_r, santa_c

### 턴 진행
for m in range(1, M+1):
    ### 기절한 산타 깨우기
    for p in range(1, P+1):
        if is_okay(p):
            continue
        elif santa_okays[p]==str(m):
            santa_okays[p]="ok"
        else:
            continue

    ##  루돌프의 움직임
    for p in range(1, P+1):
        if santa_okays[p] != "0":  # 탈락 아닌 산타
            santa_distances[p] = get_distance(p)

    min_distance = min(santa_distances)
    min_indices = [index for index, value in enumerate(santa_distances) if value == min_distance]

    max_r = -1
    max_c = -1
    target_santa = None

    if len(min_indices) > 1:
        for p in min_indices:
            r, c = get_location(p)
            if r > max_r or (r == max_r and c > max_c):
                max_r = r
                max_c = c
                target_santa = p
    else:
        target_santa = min_indices[0]
        max_r, max_c = get_location(target_santa)

    # 루돌프가 한 칸 이동
    roo_r, roo_c = roo
    dx, dy = 0, 0
    board[roo_r][roo_c] = -1

    if roo_r < max_r:
        dx = 1
    elif roo_r > max_r:
        dx = -1
    if roo_c < max_c:
        dy = 1
    elif roo_c > max_c:
        dy = -1
    roo_r += dx
    roo_c += dy

    # 충돌을 한 경우
    if board[roo_r][roo_c] == target_santa:
        # 기절시키기
        santa_okays[target_santa] = str(m+2)

        # C만큼 밀려남
        santa_r, santa_c = roo_r, roo_c
        santa_scores[target_santa] += C
        santa_r += C*dx
        santa_c += C*dy

        while True:
            # case1: 착지 불가 => 탈락
            if santa_r > N or santa_c > N or santa_r < 1 or santa_c < 1:
                santa_okays[target_santa] = "0"
                santa_distances[target_santa] = 2 * N ** 2
                break  # 루프 종료

            # case2: 무사 착지
            elif board[santa_r][santa_c] == -1:
                board[santa_r][santa_c] = target_santa
                break  # 루프 종료

            # case3: 착지 가능 & 다른 산타 => 연쇄작용
            else:
                # 현재 위치의 산타와 교체
                temp = board[santa_r][santa_c]  # 기존 산타
                board[santa_r][santa_c] = target_santa  # 현재 산타가 위치 차지
                target_santa = temp  # 밀려난 산타를 새로운 타깃 산타로 설정

                # 밀려난 산타의 위치 업데이트
                santa_r += dx
                santa_c += dy
                # 루프를 통해 다시 검사

    # 루돌프는 늘 정상착지
    roo = [roo_r, roo_c]
    board[roo_r][roo_c] = 0

    # 산타의 움직임
    for p in range(1, P+1):
        if is_okay(p):
            now_d = get_distance(p)
            p_r, p_c = get_location(p)

            # 가까운 방향 구하기
            min_dx, min_dy = 0, 0
            min_d = now_d
            # case1. 상
            dx, dy = -1, 0
            later_d = (roo_r - p_r - dx) ** 2 + (roo_c - p_c - dy) ** 2
            if later_d < min_d and p_r+dx > 0 and (board[p_r+dx][p_c+dy] == -1 or board[p_r+dx][p_c+dy] == 0):
                min_dx, min_dy = dx, dy
                min_d = later_d
            # case2. 우
            dx, dy = 0, 1
            later_d = (roo_r - p_r - dx) ** 2 + (roo_c - p_c - dy) ** 2
            if later_d < min_d and p_c + dy <= N and (board[p_r + dx][p_c + dy] == -1 or board[p_r + dx][p_c + dy] == 0):
                min_dx, min_dy = dx, dy
                min_d = later_d
            # case3. 하
            dx, dy = 1, 0
            later_d = (roo_r - p_r - dx) ** 2 + (roo_c - p_c - dy) ** 2
            if later_d < min_d and p_r + dx <= N and (board[p_r + dx][p_c + dy] == -1 or board[p_r + dx][p_c + dy] == 0):
                min_dx, min_dy = dx, dy
                min_d = later_d
            # case4. 좌
            dx, dy = 0, -1
            later_d = (roo_r - p_r - dx) ** 2 + (roo_c - p_c - dy) ** 2
            if later_d < min_d and p_c + dy > 0 and (board[p_r + dx][p_c + dy] == -1 or board[p_r + dx][p_c + dy] == 0):
                min_dx, min_dy = dx, dy
                min_d = later_d

            # 최종 이동 방향 결정
            dx, dy = min_dx, min_dy

            # 이동
            board[p_r][p_c] = -1
            p_r, p_c = p_r+dx, p_c+dy
            target_santa = p

            while True:
                # case1: 착지 불가 => 탈락
                if p_r > N or p_c > N or p_r < 1 or p_c < 1:
                    santa_okays[target_santa] = str(0)  # Fail
                    santa_distances[target_santa] = 2 * N ** 2
                    break  # 루프 종료

                # case2: 무사 착지
                elif board[p_r][p_c] == -1:
                    board[p_r][p_c] = target_santa
                    break  # 루프 종료

                # case3: 착지 but 루돌프랑 충돌 => 연쇄작용
                elif board[p_r][p_c] == 0:
                    santa_okays[target_santa] = str(m+2)
                    # 반대방향으로 D칸 밀려난 산타의 위치 업데이트
                    p_r -= dx*D
                    p_c -= dy*D
                    santa_scores[target_santa] += D
                    # 루프를 통해 다시 검사

                # case4: 착지 but 다른 산타랑 충돌 => 연쇄작용
                else:
                    # 현재 위치의 산타와 교체
                    temp = board[p_r][p_c]  # 기존 산타
                    board[p_r][p_c] = target_santa  # 현재 산타가 위치 차지
                    target_santa = temp  # 밀려난 산타를 새로운 타깃 산타로 설정

                    # 밀려난 산타의 위치 업데이트
                    p_r -= dx
                    p_c -= dy
                    # 루프를 통해 다시 검사

    # 탈락하지 않은 산타 1점씩
    for p in range(1, P+1):
        if santa_okays[p] != "0":
            santa_scores[p] += 1

    # 모두 탈락한 경우 게임 즉시 종료
    if all(status == "0" for status in santa_okays[1:]):
        break

### 결과 최종 출력
print(' '.join([str(score) for score in santa_scores[1:]]))