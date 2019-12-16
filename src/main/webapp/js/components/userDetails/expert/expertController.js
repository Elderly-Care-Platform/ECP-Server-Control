adminControllers
	.controller(
		'expertCtrl', [
			'$scope',
			'$routeParams',
			'$location',
			'UserProfile',
			'$http',
			'$rootScope',
			function($scope, $routeParams, $location,
				UserProfile, $http, $rootScope) {
				$scope.userId = $scope.$parent.userId;
				$scope.selectedServices = {};
				$scope.categories = {};
				$scope.selCategories = [];
				$scope.submitted = false;
				

				//Get Ask Question Categories
				$scope.getCategories = function(element) {
					$http.get("api/v1/ask/category/list")
						.success(function(response) {
							if (response) {
								$scope.categories = response.data;
								console.log("//////////////////////////////////////////////////",$scope.categories);
								$scope.profile = UserProfile.get({
									userId: $scope.userId
								}, function(profile) {
									$scope.profile = profile.data;
									$scope.selCategories = $scope.profile.experties;
									if(!$scope.selCategories){
										$scope.selCategories = []
									}
									console.log("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",$scope.selCategories);
								});
								
							}
						});
				};
				$scope.initData = function() {
					$scope.getCategories();
					
				};

				
				//Select menu from accordion
				$scope.selectTag = function(event, category) {
					if (event.target.checked) {
						if($scope.selCategories.indexOf(category.id)==-1){
							$scope.selCategories[ $scope.selCategories.length] = category.id;
						}
					} else {
						$scope.selCategories.splice( $scope.selCategories.indexOf(category.id), 1 );
					}
				}


				
				//Post individual form
				$scope.postUserProfile = function(isValidForm) {
					$(".by_btn_submit").prop("disabled", true);
					$scope.submitted = true;
					var experties = $.map($scope.selCategories, function(value, key) {
						return {id: value};
					});
					if(!experties){
						experties = [];
					}
					$scope.profile.experties = experties;
					
					if (isValidForm.$invalid) {
						window.scrollTo(0, 0);
						$(".by_btn_submit").prop('disabled', false);
					} else {
						
						var userProfile = new UserProfile();
						angular.extend(userProfile, $scope.profile);
						userProfile.$update({
							userId: $scope.userId
						}, function(profileOld) {
							console.log("success");
							$scope.submitted = false;
							$location.path('/userDetails/' + $scope.userId);
						}, function(err) {
							console.log(err);
						});
					}
				}

				$scope.cancel = function() {
					$location.path('/userDetailType/' + $scope.userId);
				}
			}

		]);