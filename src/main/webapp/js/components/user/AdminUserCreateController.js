


adminControllers.controller('AdminUserCreateController', ['$scope', '$routeParams', '$location', 'AdminUser', 'UserTag',
  function($scope, $routeParams, $location, AdminUser, UserTag) {
	  	if(localStorage.getItem("ADMIN_USER_ROLE") == 'WRITER' || localStorage.getItem("ADMIN_USER_ROLE") == 'USER' || localStorage.getItem("ADMIN_USER_ROLE") == '' || localStorage.getItem("ADMIN_USER_ROLE") == 'EDITOR')
	  			 {
	  				 return;
	 }
	  	$scope.customError = {
	  		"passError":""	
	  	};
     var userId = $routeParams.userId;
     	if(userId != null )
	 	{
	 		UserTag.get(function(res) {
				$scope.existingTags = res.data;
			}, function(errorResponse) {
				if (errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002) {
					$location.path('/users/login');
					return;
				}
				alert("error fetching tags");
			});

	 		AdminUser.get({userId: userId},function(res){
	 			$scope.user = res.data;
	 			$scope.userPassword = "";
	 			$scope.allTags =[];
	 			 /*for (var i = 0; i < $scope.user.userTags.length; i++) {
		            if ($scope.user.userTags[i] === $scope.existingTags) {
		                
		                $scope.existingTags[i].push($scope.allTags);
		            }
		        };*/

		       Object.keys($scope.user.userTags).forEach(function(key){
		       	if($scope.user.userTags[key]){
		       		for ( var i = 0; i < $scope.existingTags.length; i++){
		       			if($scope.user.userTags[key] == $scope.existingTags[i].id){
		       				$scope.allTags.push($scope.existingTags[i]);
		       			}
		       		}
		       		
		       	}
				});

		       $scope.allTags.push("");


	 			if($scope.user.regType ==null || $scope.user.regType == undefined){
	 				$scope.user.regType = 0;
	 			}
	 			if($scope.user.phoneNumber ==null || $scope.user.phoneNumber == undefined){
	 				$scope.user.phoneNumber = "";
	 			}else if($scope.user.email ==null || $scope.user.email == undefined){
	 				$scope.user.email = "";
	 			}
	 		},function(errorResponse){
	 			if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
 					$location.path('/users/login');
 					 return;
 		        }
	 		});

	 		

			$scope.allTags = [ "" ];
	 		
	 		$scope.addTag = function() {
				$scope.allTags.push("");
			}
			$scope.removeTag = function(idx) {
				$scope.allTags.splice(idx, 1);
			}

	 		$scope.edituser = function () {
	 			if($scope.userPassword != "" ){
	 				if($scope.user.userRegType != 0){
	 					$scope.customError.passError = "Password field is not consodered for this user type";
	 					return;
	 				}else if($scope.userPassword.length < 6){
	 					$scope.customError.passError = "minimum length of password should be 6";
	 					return;
	 				}else{
	 					$scope.user.password = $scope.userPassword;
	 				}
	 			}
	 			$scope.existingTags = [];
	 			for(var i = 0; i < $scope.allTags.length; i++){
	 				if($scope.allTags[i] && $scope.allTags[i].id){
	 					$scope.existingTags.push($scope.allTags[i].id);
	 				}
	 			}
	 			$scope.user.userTags =  $scope.existingTags;
	 			AdminUser.update($scope.user,function(res){
		 			toastr.success("Edited User");
	 				$location.path('/users/all');
		 		},function(errorResponse){
		 			if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
	 					$location.path('/users/login');
	 					 return;
	 		        }
		 			if(error && error.data && error.data.error && error.data.error.errorCode){
	 					$scope.error = error.data.error.errorMsg;
	 				}
		 		});
	 		};
	 	}
	 	else
	 	{
	 		$scope.user = new AdminUser();
	 		$scope.user.regType = 0;

			$scope.register = function () {
				if($scope.userForm.$invalid) return;
				AdminUser.update($scope.user,function(res){
					$scope.message = "User registered successfully";
					$scope.error = '';
					$scope.submitted = true;
					$location.path('/users/all');
		 		},function(errorResponse){
		 			if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
	 					$location.path('/users/login');
	 					 return;
	 		        }
		 			console.log("$save failed " + JSON.stringify(error));
					$scope.error = 'Error in registering.Check your inputs and try again. Make sure that the Email is unique to the system.';
					$scope.message = '';
					$scope.submitted = false;
					$scope.userName = '';
					$scope.email = '';
					$scope.password = '';
					$scope.userRoleId = '';

					$location.path('/users/new');
		 		});

			};
		}
  }]);