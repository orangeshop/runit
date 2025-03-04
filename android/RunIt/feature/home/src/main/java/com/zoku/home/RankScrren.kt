package com.zoku.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.zoku.home.viewmodel.RankViewModel
import com.zoku.network.model.response.AttendanceDay
import com.zoku.network.model.response.GroupMember
import com.zoku.ui.componenet.CustomDialog
import com.zoku.ui.componenet.ShowExpInfo
import com.zoku.ui.componenet.ShowGroupInfo
import com.zoku.ui.componenet.ShowModalBottomSheet
import com.zoku.ui.model.ExpInfoData
import com.zoku.ui.theme.BaseDarkBackground
import com.zoku.ui.theme.CustomTypo
import com.zoku.ui.theme.League
import com.zoku.ui.theme.RankProfile
import com.zoku.ui.theme.ZokuFamily
import com.zoku.ui.theme.leagueList
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankScreen(
    modifier: Modifier = Modifier,
    moveToExpHistory: () -> Unit,
    rankViewModel: RankViewModel = hiltViewModel()
) {
    val rankMenu = arrayOf("종합 순위", "페이스 순위", "거리 순위")

    val myName by rankViewModel.userName.collectAsState()
    // 이번주 출석 현황
    val attendanceList by rankViewModel.attendanceWeekInfo.collectAsState()
    // 이번주 획득 경험치
    val weekExp by rankViewModel.currentExp.collectAsState()

    // 그룹 리스트
    val groupList by rankViewModel.groupInfo.collectAsState()

    var listType by remember {
        mutableStateOf(0)
    }

    with(rankViewModel) {
        when (listType) {
            0 -> getGroupList("experience")
            1 -> getGroupList("pace")
            2 -> getGroupList("distance")
        }
    }
    //GroupInfo BottomSheet
    var isShowGroupInfo by remember { mutableStateOf(false) }
    var isShowExpInfo by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    Column(
        modifier =
        modifier
            .padding(horizontal = 10.dp)
            .fillMaxSize(),
    ) {
        RankingInfo(
            moveToExpHistory,
            weekExp,
            attendanceList,
            myName,
            groupList.rank,
            leagueList[groupList.leagueRank - 1],
            onShowGroupInfo = {
                isShowGroupInfo = true
            },
            onShowExpInfo = {
                isShowExpInfo = true
            }
        )

        HomeTitle(
            modifier.padding(top = 10.dp, bottom = 5.dp),
            "그룹 내 순위",
            "종합 순위",
            rankMenu,
        ) { selectedOption ->
            if (selectedOption == "그룹 내 순위" + " 종합 순위") {
                listType = 0
            } else if (selectedOption == "그룹 내 순위" + " 페이스 순위") {
                listType = 1
            } else {
                listType = 2
            }
        }

        if (groupList.userInfos.isNotEmpty()) {
            UserRanking(
                groupList.userInfos,
                (groupList.leagueRank - 1),
            )
        }

        if (isShowGroupInfo) {
            ShowGroupInfoBottomSheet(sheetState = modalBottomSheetState, onDismiss = {
                isShowGroupInfo = false
            })
        }

        if (isShowExpInfo) {
            ShowExpInfoDialog() {
                isShowExpInfo = false
            }
        }
    }
}

@Composable
fun RankingInfo(
    moveToExpHistory: () -> Unit,
    weekExp: Int,
    attendanceList: List<AttendanceDay>,
    myName: String,
    myRank: Int,
    league: League,
    onShowGroupInfo: (Boolean) -> Unit,
    onShowExpInfo: (Boolean) -> Unit
) {
    val baseModifier = Modifier.fillMaxWidth()
    Box(
        modifier =
        baseModifier
            .clip(RoundedCornerShape(15.dp))
            .background(BaseDarkBackground),
    ) {
        Column(
            modifier = baseModifier,
        ) {
            InfoIconButton("리그 정보", onClick = {
                onShowGroupInfo(true)
            })

            UserProfile(myName, league)
            if (attendanceList.isNotEmpty()) {
                DailyCheckView(
                    baseModifier
                        .padding(top = 10.dp),
                    attendanceList,
                )
            }

            ExpView(baseModifier, moveToExpHistory, weekExp, myRank)

            InfoIconButton("경험치 획득 방법", onClick = {
                onShowExpInfo(true)
            })
        }
    }
}

@Composable
fun UserProfile(
    myName: String,
    league: League,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Column {
            RankText(
                text = "환영합니다! 오늘도 화이팅 :)",
                fontSize = 12.sp,
            )
            Row(
                modifier = Modifier.padding(top = 20.dp),
            ) {
                Text(
                    text = myName,
                    style =
                    CustomTypo().mapleBold.copy(
                        color = Color.White,
                        fontSize = 25.sp,
                    ),
                )
                RankText(
                    text = "님",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Bottom),
                )
            }
        }
        Text(
            text = "현재 ",
            modifier =
            Modifier
                .weight(1f)
                .align(Alignment.Bottom),
            style =
            CustomTypo().jalnan.copy(
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.End,
            ),
        )

        Text(
            text = league.name,
            modifier =
            Modifier
                .align(Alignment.Bottom),
            style =
            CustomTypo().jalnan.copy(
                textAlign = TextAlign.End,
                fontSize = 14.sp,
                color = Color(league.color),
                fontFamily = ZokuFamily,
            ),
        )
        Image(
            painter = painterResource(id = league.imageUrl),
            contentDescription = null,
            modifier =
            Modifier
                .width(60.dp)
                .height(60.dp)
                .align(Alignment.Bottom),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun ShowExpInfoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val list = ExpInfoData.DEFAULT
    val pagerState = rememberPagerState(
        pageCount = { list.size }
    )
    CustomDialog(
        onClickOk = {
            onDismiss()
        },
        composable = {
            ShowExpInfo(pagerState = pagerState,
                onOkClick = {
                    onDismiss()
                })
        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowGroupInfoBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ShowModalBottomSheet(
        sheetState = sheetState,
        onDismiss = {
            onDismiss()
        },
        composable = {
            ShowGroupInfo()
        }
    )

}

@Composable
fun InfoIconButton(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 10.sp,
            fontFamily = ZokuFamily,
            color = Color.White,
            textAlign = TextAlign.End,
        )
        IconButton(
            onClick = { onClick() },
        ) {
            Image(
                painter = painterResource(id = R.drawable.info_rank_icon),
                contentDescription = null,
                modifier =
                Modifier
                    .width(13.dp)
                    .height(13.dp),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun DailyCheckView(
    modifier: Modifier = Modifier,
    attendanceList: List<AttendanceDay>,
) {
    val today = LocalDate.now().dayOfWeek.value - 1
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        for (i in 0..today) {
            DailyCheck(
                Modifier.weight(1f),
                day = attendanceList[i].day.substring(0, 1),
                type = if (attendanceList[i].attended) 1 else 2,
            )
        }
        for (i in today + 1 until 7) {
            DailyCheck(
                Modifier.weight(1f),
                day = attendanceList[i].day.substring(0, 1),
                type = 0,
            )
        }
    }
}

@Composable
fun DailyCheck(
    modifier: Modifier = Modifier,
    type: Int = 0,
    day: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter =
            painterResource(
                id =
                when (type) {
                    0 -> R.drawable.none_rank_icon
                    1 -> R.drawable.success_rank_icon
                    else -> R.drawable.fail_rank_icon
                },
            ),
            contentDescription = null,
            modifier =
            Modifier
                .width(25.dp)
                .height(35.dp)
                .padding(bottom = 10.dp),
        )
        RankText(text = day)
    }
}

@Composable
fun ExpView(
    modifier: Modifier = Modifier,
    moveToExpHistory: () -> Unit,
    weekExp: Int,
    myRank: Int,
) {
    Row(
        modifier =
        modifier
            .padding(vertical = 5.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            Row {
                Surface(
                    color = Color.Transparent,
                    onClick = {
                        moveToExpHistory()
                    },
                ) {
                    RankText(text = "획득경험치")
                }
                Image(
                    painter = painterResource(id = R.drawable.next_run_history_icon),
                    contentDescription = null,
                    modifier =
                    Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .align(Alignment.CenterVertically),
                )
            }

            RankText(text = "$weekExp xp")
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            RankText(text = "현재순위")
            RankText(text = "${myRank}위")
        }
    }
}

@Composable
fun UserRanking(
    groupList: List<GroupMember>,
    rank: Int,
) {
    val groupSize = groupList.size
    val promoteCount = ceil(groupSize * 0.3).toInt()
    val demoteCount = floor(groupSize * 0.3).toInt()

    LazyColumn(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    ) {
        // 승급 인원
        items(promoteCount) { index ->
            val item = groupList[index]
            UserRankingProfile(
                Modifier
                    .fillMaxWidth(),
                item,
                index,
            )
        }

        // 승급 표시, 알이면 없음
        if (rank > 0) {
            item {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                ) {
                    Box(
                        modifier =
                        Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.up_rank_icon),
                            contentDescription = null,
                            modifier =
                            Modifier
                                .width(20.dp)
                                .height(20.dp),
                        )
                    }
                    Text(
                        text = "승급",
                        style =
                        CustomTypo().jalnan.copy(
                            color = Color.Green,
                            fontSize = 24.sp,
                        ),
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.up_rank_icon),
                            contentDescription = null,
                            modifier =
                            Modifier
                                .width(20.dp)
                                .height(20.dp),
                        )
                    }
                }
            }
        }
        // 유지 인원
        items(groupSize - promoteCount - demoteCount) { index ->
            val item = groupList[promoteCount + index]
            UserRankingProfile(
                Modifier
                    .fillMaxWidth(),
                item,
                promoteCount + index,
            )
        }

        if (demoteCount > 0) {
            // 강등 알이면 없음
            if (rank > 0) {
                item {
                    Row(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                    ) {
                        Box(
                            modifier =
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.down_rank_icon),
                                contentDescription = null,
                                modifier =
                                Modifier
                                    .width(20.dp)
                                    .height(20.dp),
                            )
                        }
                        Text(
                            text = "강등",
                            style =
                            CustomTypo().jalnan.copy(
                                color = Color.Red,
                                fontSize = 24.sp,
                            ),
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.down_rank_icon),
                                contentDescription = null,
                                modifier =
                                Modifier
                                    .width(20.dp)
                                    .height(20.dp),
                            )
                        }
                    }
                }
            }

            // 강등 인원
            items(demoteCount) { index ->
                val item = groupList[groupSize - demoteCount + index]
                UserRankingProfile(
                    Modifier
                        .fillMaxWidth(),
                    item,
                    groupSize - demoteCount + index,
                )
            }
        }
    }
}

@Composable
fun UserRankingProfile(
    modifier: Modifier = Modifier,
    item: GroupMember,
    index: Int,
) {
    val baseModifier = Modifier.fillMaxHeight()

    Surface(
        color = BaseDarkBackground,
        modifier =
        modifier
            .height(80.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp)),
    ) {
        Row {
            Box(
                modifier =
                baseModifier
                    .padding(start = 10.dp)
                    .wrapContentWidth(),
                contentAlignment = Alignment.Center,
            ) {
                RankText(
                    text = "${index + 1}",
                    fontSize = 24.sp,
                )
            }
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                baseModifier
                    .size(80.dp) // 명확한 크기 설정
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(50.dp)),
            )
            Box(
                modifier = baseModifier.wrapContentWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                RankText(text = item.userName, fontSize = 20.sp)
            }
            Box(
                modifier = baseModifier.weight(1f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                RankText(
                    text = "${item.score}",
                    fontSize = 24.sp,
                )
            }
            val iconRes =
                if (item.rankDiff > 0) {
                    R.drawable.up_rank_icon
                } else if (item.rankDiff == 0) {
                    R.drawable.stop_icon
                } else {
                    R.drawable.down_rank_icon
                }

            val rankColor =
                if (item.rankDiff > 0) {
                    Color.Green
                } else if (item.rankDiff == 0) {
                    RankProfile
                } else {
                    Color.Red
                }

            Box(
                modifier = baseModifier,
                contentAlignment = Alignment.CenterEnd,
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier =
                    baseModifier
                        .size(50.dp) // 명확한 크기 설정
                        .padding(top = 15.dp, bottom = 15.dp, start = 20.dp, end = 8.dp),
                )
            }
            Box(
                modifier = baseModifier,
                contentAlignment = Alignment.CenterEnd,
            ) {
                RankText(
                    text = item.rankDiff.toString(),
                    fontSize = 12.sp,
                    modifier =
                    Modifier
                        .width(20.dp)
                        .padding(end = 10.dp),
                    color = rankColor,
                )
            }
        }
    }
}

@Composable
fun RankText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 15.sp,
    color: Color = Color.White,
) {
    Text(
        modifier = modifier,
        text = text,
        style =
        CustomTypo().jalnan.copy(
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            color = color,
        ),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, apiLevel = 34)
@Composable
fun PreviewGroupInfoBottomSheet(
    modifier: Modifier = Modifier
) {
    var state = rememberModalBottomSheetState()
    ShowGroupInfoBottomSheet(sheetState = state, onDismiss = {})
}
