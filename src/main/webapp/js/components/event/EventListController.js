'use strict';

(function (angular) {
    'use strict';

    adminControllers
            .controller('EventListController', EventListController)
            .factory('EventList', EventListFactory);
    
    EventListController.$inject = ['$scope', '$http', '$q', '$location', '$resource', 'DTOptionsBuilder', 'EventList'];

    function EventListController($scope, $http, $q, $location, $resource, DTOptionsBuilder, EventList) {
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
        
        
        EventList.getEventLists().then(function(EventLists) {
        	vm.myEventLists = EventLists;
        });
    }
    
    EventListFactory.$inject = ["$http"];
  
    function EventListFactory($http) {
    	return {
    		getEventLists: function() {	
    			var pathName = location.pathname;
    			return $http.get(pathName + 'api/v1/event/list').then(function(response) {
    				return response.data.data;
    			});
    		}
    	};
    }

  
}(angular));





