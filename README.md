# 뭐먹었지도 | Personal AI FoodMap
<p>&nbsp;본 프로젝트는 2021년 1,2학기 '캡스톤디자인' 수업을 통해 진행한 프로젝트입니다. </p>

## 프로젝트 소개
<p>&nbsp;팀원들 모두 지인들에게 식당을 추천할 때, 자신이 갔을 때의 사진을 보며 설명해야하는 경험을 종종 겪어 왔습니다. <br>
하지만 갤러리의 수많은 사진들 중에서 음식 사진만을 추려내는 것은 쉽지 않은 과정이었고, <br>
이 중에서도 '어디서 뭘 먹었는지' 떠올리는 것은 더 어려운 일이었습니다. </p>

<p>&nbsp;이러한 일상 속 어려움에서 아이디어를 고안해 갤러리에서 음식 사진만을 찾아 지도상에 표시해주고 <br>
식당을 찾아주는 '뭐먹었지도'를 개발하게 되었습니다.</p> 

## 진행 기간
2021년 3월 ~ 2021년 11월

## 팀원들
<table align="center" border="1.5" bordercolor="gray">
    <tr>
        <td align="center"><a href="https://github.com/Ryeoryeon"><img src="https://avatars3.githubusercontent.com/u/50348995?s=500&u=7484588e133e5efa66f6cd14dac2417a90a4f598&v=4" width="180px;" alt=""/><br/><sub><b>이영현()</b></sub></a></td>
        <td align="center"><a href="https://github.com/jinjinzara"><img src="https://avatars.githubusercontent.com/u/82082271?v=4" width="180px;" alt=""/><br/><sub><b>진소연()</b></sub></a></td>
        <td align="center"><a href="https://github.com/gemiJ"><img src="https://avatars.githubusercontent.com/u/30407907?v=4" width="180px;" alt=""/><br/><sub><b>주윤겸()</b></sub></a></td>
    </tr>
</table>

## 주요기능
<p> 1. 음식 검출 모델 생성 및 학습 </p>
<img src="https://user-images.githubusercontent.com/30407907/141249744-4f231ad1-2819-4ab3-bdb9-c890950bd393.png"  width="180" height="150" /> 
<p> 2. 제작한 모델을 통해 사용자의 갤러리에서 음식 사진만을 검출 <br>
3. 검출한 음식 사진들을 지도에 표시 <br>
4. 지도의 확대 정도에 따라 사진이 흩어지고 합쳐지는 marker clustering </p>
<img src="https://user-images.githubusercontent.com/30407907/141252951-497b84b5-48a7-464d-a1bf-f72dd81f4fff.gif"  width="180" height="300" />
<p>
5. 사진의 위치 정보에 따라 식당의 세부 정보 표시 <br>
6. 올바른 식당명을 내부 DB에 저장하는 기능 <br>
7. 카테고리 별 근처 식당 추천 기능 </p>
<img src="https://user-images.githubusercontent.com/30407907/141254268-38251d51-2004-4251-afa2-20c5b05b472c.png"  width="180" height="300" /> 

## 프로젝트 구조
<img src="https://user-images.githubusercontent.com/30407907/141255718-9521fc9a-dbe1-484d-b0e4-3e128bc47d44.png"  width="230" height="400" /> 

## 사용기술
+ OpenCV
+ Darknet, YOLOv3
+ AAC MVVM
+ Glide
+ Google Maps API
+ Kakao Local API
+ 협업 툴 : Figma, Git, Google Drive
