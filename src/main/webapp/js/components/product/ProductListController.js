'use strict';

(function (angular) {
    'use strict';

    adminControllers
            .controller('ProductListController', ProductListController)
            .factory('ProductList', ProductListFactory);
    
    ProductListController.$inject = ['$scope', '$http', '$q', '$location', '$resource', 'DTOptionsBuilder', 'ProductList'];

    function ProductListController($scope, $http, $q, $location, $resource, DTOptionsBuilder, ProductList) {
        var vm = this;     
        
        if (localStorage.getItem("ADMIN_USER_ROLE") !== 'SUPER_USER' && localStorage.getItem("ADMIN_USER_ROLE") !== 'EDITOR') {
    		$location.path('/users/login');
    		return;
    	}
        
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withPaginationType('full_numbers')
			.withDisplayLength(100)
			.withOption("bAutoWidth", false)
			.withOption('searchable', false)
        
        $scope.host = location.host;
        $scope.pathName = location.pathname;
        
        
        ProductList.getProductLists().then(function(ProductLists) {
        	vm.myProductLists = ProductLists;
        });
    }
    
    ProductListFactory.$inject = ["$http"];
  
    function ProductListFactory($http) {
    	return {
    		getProductLists: function() {	
    			var pathName = location.pathname;
    			return $http.get(pathName + 'api/v1/product/list').then(function(response) {
    				return response.data.data;
    			});
    		}
    	};
    }

  
}(angular));





