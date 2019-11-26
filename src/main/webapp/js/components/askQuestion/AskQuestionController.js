adminControllers.controller('AskQuestionController', [ '$scope',
		'$routeParams', '$location','$http',
		function($scope, $routeParams, $location, $http) {
			$scope.error = false;
			var id = $routeParams.askQuestionId;
			$scope.ask = {
				id: "",
				question: "",
				description:"",
				askCategory:"",
				answeredBy:"",
				askedBy:"",
				answered:false
			};
			$scope.categories = [];
			$http.get("api/v1/ask/category/list").success(
				function(response) {
					response = response.data;
					if(response != null && response !== ""){
						$scope.categories = response;
					}else{
						$scope.error = true;
						$scope.errorMessage = "No Categories found";
					}
				}, function(errorResponse) {
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
					}
				});
			if(id != "new"){
				$scope.ask.id = id;
				$http.get("api/v1/ask?askQuesId="+id).success(
					function(response) {
						response = response.data;
						if(response != null && response !== ""){
							$scope.ask = response;
						}else{
							$scope.error = true;
							$scope.errorMessage = "No AskQuestion found";
						}
					}, function(errorResponse) {
						if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
							$location.path('/users/login');
							 return;
				        }
					});
			}
			
			$scope.editAskQuestion = function(){
				var prod = JSON.parse(JSON.stringify($scope.ask));
				$http.put("api/v1/ask",prod).success(function(res){
					toastr.success('AskQuestion submitted successfully');
					$location.path('/askQuestions');
				}).error(function(errorResponse){
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
			        }
					$scope.error = true;
					$scope.errorMessage = "Error occured in saving the current ask.";
				})
			}

			$scope.addAskQuestion = function(){
				var prod = JSON.parse(JSON.stringify($scope.ask));
				delete prod.id;
				$http.post("api/v1/ask",prod).success(function(res){
					toastr.success('AskQuestion submitted successfully');
					$location.path('/askQuestions');
				}).error(function(errorResponse){
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
			        }
					$scope.error = true;
					$scope.errorMessage = "Error occured in saving the current ask.";
				})
			}
		} ]);