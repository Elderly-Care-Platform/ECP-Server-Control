adminControllers.controller('AskQuestionCategoryController', [ '$scope',
	'$routeParams', '$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.error = false;
		var id = $routeParams.askQuestionCategoryId;
		$scope.askQuestionCategory = {
			id: "",
			name: ""
		};
		if(id != "new"){
			$scope.askQuestionCategory.id = id;
			$http.get("api/v1/ask/category?askCategoryId="+id).success(
				function(response) {
					response = response.data;
					if(response != null && response !== ""){
						$scope.askCategory = response;
					}else{
						$scope.error = true;
						$scope.errorMessage = "No AskQuestion Category found";
					}
				}, function(errorResponse) {
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
					}
				});
		}

		$scope.editAskQuestionCategory = function(){
			var prod = JSON.parse(JSON.stringify($scope.askCategory));
			$http.put("api/v1/ask/category/",prod).success(function(res){
				toastr.success('AskQuestion category submitted successfully');
				$location.path('/askQuestions/category/list');
			}).error(function(errorResponse){
				if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
					$location.path('/users/login');
					 return;
				}
				$scope.error = true;
				$scope.errorMessage = "Error occured in saving the current askCategory.";
			})
		}

		$scope.addAskQuestionCategory = function(){
			var prod = JSON.parse(JSON.stringify($scope.askCategory));
			delete prod.id;
			$http.post("api/v1/ask/category",prod).success(function(res){
				toastr.success('AskQuestion Category submitted successfully');
				$location.path('/askQuestions/category/list');
			}).error(function(errorResponse){
				if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
					$location.path('/users/login');
					 return;
				}
				$scope.error = true;
				$scope.errorMessage = "Error occured in saving the current ask category.";
			})
		}
	}
]);
