<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<main id="main">

	<form class="form" action="reg" method="post"
		enctype="multipart/form-data">
		<table border="1">
			<tr>
				<th>감정선택</th>
				<td colspan="3">감정이모티콘뷰</td>
			</tr>
			<tr>
				<th>질문선택</th>
				<td colspan="3">질문선택지</td>
			</tr>
			<tr>
				<th>첨부파일</th>
				<td colspan="3"><input type="file" name="f"></td>
			</tr>
			<tr>
				<th>공개선택</th>
				<td colspan="3">공개 / 비공개</td>
			</tr>
			<tr>
				<td colspan="4">
					<div contenteditable="true"></div> <textarea rows="20" cols="50"
						name="content">자유롭게 글을 써보세요</textarea>

				</td>
			</tr>
		</table>


		<div>
			<input type="submit" value="저장"> <a href="list">취소</a>
		</div>
	</form>
	</section>

</main>