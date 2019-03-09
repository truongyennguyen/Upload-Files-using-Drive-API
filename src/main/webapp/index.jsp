<!DOCTYPE html>
<html lang="en">
  <head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
 
    <title>Upload Files using API</title>
    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  </head>
  <body>
   <div class="container-fluid" >   	  
      <div class="row" style="margin-top: 50px;">      
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">     
          <h2 class="sub-header">Upload file</h2>
		  <form id="code" method="post" enctype="multipart/form-data" class="form-horizontal">
			<div class="input-group">
				<input id="file-upload" type="file" name="file-upload">
			</div>
		  </form><br>
		  <button type="button" id="btn-import" class="btn btn-default">Upload</button>	
		  <div style="height: 20px"></div>
		  <p id="msg_uploading">Uploading...</p>
		  <p id="msg_link">Your file: <a href="#" id="link_file"></a></p>	  
          </div>
        </div>
      </div>
    </div>
  </body>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
  <script type="text/javascript">  
	$(document).ready(function() {
		$("#msg_uploading").hide();
		$("#msg_link").hide();
		$("#btn-import").click(function() {

			var file = $('#file-upload').get()[0].files[0];
			var fileUpload = $('#file-upload').val();
			if(!file){
				alert("Vui lòng chọn file");
				return;
			}else{
				$("#msg_uploading").show();
			}
 
			$.ajax({
				url: 'UploadHandle',
				type: 'POST',
				data: new FormData($('#code')[0]),
				processData: false,
				contentType: false,
				success : function(msg){
					$("#link_file").attr("href", msg);
					$("#link_file").text(msg);
					$("#msg_uploading").hide();
					$("#msg_link").show();
				}
			}).done(function () {
				alert("Upload thành công");
			});
		});
	});	
  </script>
</html>